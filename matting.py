import cv2
import numpy as np
import scipy.sparse as sparse
from scipy.sparse import linalg

def sigmoid(x: np.array):
    return 1 / (1 + np.exp(-10*(x - 0.5)))

class CloseFormMatting():
    
    # epsilon = 10e-7
    epsilon=1

    thresh_lower=0.05
    thresh_upper=0.95

    def __init__(self, win_size: int = 3):
        self.win_size=win_size
        self.sqrt_epsilon = np.sqrt(self.epsilon)

    def run(self, img: np.ndarray, alpha: np.array):

        indicator = (alpha > 0.05) & (alpha < 0.95)
        unknown_len = indicator.sum()
        unknown_indexes=np.where(indicator == True)[0]
        idx_map = dict(zip(unknown_indexes, range(unknown_len)))

        L, b = self.__L_func(img, alpha)
        L11, b1 = self.__L11_func(L, b, idx_map, unknown_len, unknown_indexes)

        solu = linalg.spsolve(L11, b1)
        solu = np.clip(solu, 0, 1)

        alpha[unknown_indexes] = solu

        gray = alpha.reshape(img.shape[:2])
        gray = (gray * 255).astype(np.uint8)

        ## 生成抠图
        b, g, r = cv2.split(img)
        matting = cv2.merge([b, g, r, gray])

        return gray, matting

    def __Gk_func(self, img: np.ndarray, row_idx: int, col_idx: int):
        '''
            计算每个窗口的Gk
            Args:
                row_idx: 窗口左上角的行索引
                col_idx: 窗口左上角的列索引
        '''
        win_size = self.win_size

        t, b, l, r = row_idx, row_idx + win_size, col_idx, col_idx + win_size
        patch = img[t:b, l:r]
        patch = patch.reshape(-1, 3)
        Gk = np.zeros((win_size * (win_size+1), 4))
        Gk[0:win_size ** 2, 0:3] = patch
        Gk[0:win_size ** 2, 3] = 1
        tail=np.diag(np.full(win_size, self.sqrt_epsilon))
        Gk[win_size**2: win_size * (win_size + 1), 0:3] = tail

        return Gk

    def __Lk_func(self, gk: np.ndarray):
        win_size = self.win_size
        temp=np.matmul(gk.transpose(), gk)
        temp=np.linalg.inv(temp)
        temp = np.matmul(gk, temp)
        temp = np.matmul(temp, gk.transpose())
        
        I = np.diag(np.full(len(gk),1))
        gk_bar = temp - I
        lk = np.matmul(gk_bar.transpose(), gk_bar)
        lk = lk[0: win_size ** 2, 0: win_size ** 2]

        return lk

    def __k(self, i: int, r: int, c: int, w: int):
        """
            Lk 到 L 的位置映射关系
            Args:
                i: 窗口的第i个元素索引
                r: 窗口的行索引
                c: 窗口的列索引
                w: 图片宽度
        """
        win_size=self.win_size
        r_win = i // win_size
        c_win = i - r_win * win_size
        ki = (r + r_win) * w + c + c_win
        return ki

    def __L_func(self, img: np.ndarray, alpha: np.ndarray):
        """
        拉普拉斯矩阵计算方法
        Args: 
            img: 原始图片对象
            alpha: trimap 图像
            win_size: 窗口大小
        Return:
            COO 存储形式的拉普拉斯矩阵，以及 RHS
        """
        h, w = img.shape[:2]
        win_size=self.win_size

        L = {} ## COO 格式的矩阵存储容器
        b = np.zeros(h * w)
        for row in range(h - win_size + 1):
            for col in range(w - win_size + 1):

                unknonwn_win = False ## 当前窗口是否包含未知像素
                for i in range(win_size ** 2):
                    ki = self.__k(i, row, col, w)
                    if (alpha[ki] > self.thresh_lower) and (alpha[ki] < self.thresh_upper): ## 判断是否是未知像素
                        unknonwn_win = True 
                        break

                if not unknonwn_win: ## 如果当前窗口不包含未知像素，则继续下一个窗口
                    continue
            
                gk = self.__Gk_func(img, row, col)
                lk = self.__Lk_func(gk)

                for i in range(win_size ** 2):
                    ki = self.__k(i, row, col, w)
                    if (alpha[ki] > self.thresh_lower) and (alpha[ki] < self.thresh_upper): ## 只处理未知像素
                        for j in range(win_size ** 2):
                            kj = self.__k(j, row, col, w)
                            if (alpha[kj] > self.thresh_lower) and (alpha[kj] < self.thresh_upper): ## 如果当前像素是未知像素，则将其存储矩阵               
                                if (ki, kj) in L:
                                    L[(ki, kj)] = L[(ki, kj)] + lk[i, j]
                                else:
                                    L[(ki, kj)] = lk[i,j]
                            else: ## 否则用于计算 b
                                b[ki] += lk[i, j] * alpha[kj]

        return L, b

    def __L11_func(self, L: dict, b: np.ndarray, idx_map: dict, size: int, unknown_indexes: np.array):
        '''
            简化 L 矩阵计算函数
            Args:
                L:        完整拉普拉斯矩阵的简化存储形式: {(i, j): v}
                b:        完整的 RHS
                idx_map:  索引映射表
                size:     简化矩阵尺寸
                unknown_indexes: 未知像素索引
        '''
        rows_idx=[idx_map[t[0]] for t in list(L.keys())]
        cols_idx=[idx_map[t[1]] for t in list(L.keys())]
        values = list(L.values())

        L11=sparse.csr_matrix((values, (rows_idx, cols_idx)), shape=(size, size))
        b1 = -b[unknown_indexes]

        return L11, b1

    def __L_complete_func(self, img: np.ndarray):
        '''
        计算完整的拉普拉斯矩阵
        Args:
            img: 图片数据
        '''
        h, w = img.shape[:2]
        L = {}
        win_size=self.win_size
        for row in range(h - win_size + 1):
            for col in range(w - win_size + 1):
                gk = self.__Gk_func(img, row, col)
                lk = self.__Lk_func(gk)

                for i in range(win_size ** 2):
                    ki = self.__k(i, row, col, w)
                    for j in range(win_size ** 2):
                        kj = self.__k(j, row, col, w)
                        if (ki, kj) in L:
                            L[(ki, kj)] = L[(ki, kj)] + lk[i, j]
                        else:
                            L[(ki, kj)] = lk[i,j]

        rows = [t[0] for t in list(L)]
        cols = [t[1] for t in list(L)]
        values = list(L.values())

        h, w = img.shape[:2]
        size = h * w
        return sparse.csr_matrix((values, (rows, cols)), shape=(size, size))
    
    def __resize(self, img: np.ndarray, target_size: int = 200):
        h, w = img.shape[:2]
        if h > w:
            w = int(w / h * target_size)
            h = target_size
        else: 
            h = int(h / w * target_size)
            w = target_size
        
        resized = cv2.resize(img, (w, h))
        return resized

    def eigen_image(self, img: np.ndarray):
        '''
            计算给定图片的拉普拉斯矩阵的第二小特征向量
        '''
        H, W = img.shape[:2]
        h = H
        w = W
        target_size=200
        if H > target_size or W > target_size:
            img=self.__resize(img, target_size)
            h, w = img.shape[:2]
    
        L = self.__L_complete_func(img)
        _, eigenvectors = sparse.linalg.eigsh(L, 3, which="LM", sigma=0)
        ev1 = eigenvectors[:, 1].real
        ev1 = np.interp(ev1, (ev1.min(), ev1.max()), (0, 255))
        ev1 = ev1.astype(np.uint8)
        ev1 = ev1.reshape(h, w)

        ev0 = eigenvectors[:, 2].real
        ev0 = np.interp(ev0, (ev0.min(), ev0.max()), (0, 255))
        ev0 = ev0.astype(np.uint8)
        ev0 = ev0.reshape(h, w)

        if h != H:
            ev1 = self.__resize(ev1, max(H, W))
            ev0 = self.__resize(ev0, max(H, W))

        return ev0, ev1