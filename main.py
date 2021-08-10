import argparse

from scipy.sparse import base
from matting import CloseFormMatting
import cv2
import os
import numpy as np
import sys

def arg_parse():
    parser = argparse.ArgumentParser()
    parser.add_argument("--mode", dest="mode", default="matting", help="specify the task mode, it can be 'matting' or 'eigen'")
    parser.add_argument("--image", dest="image", help="image path")
    parser.add_argument("--trimap", dest="trimap", help="trimap path", default=None)
    parser.add_argument("--scribble", dest="scribble", help="scribble path", default=None)
    parser.add_argument("--output_dir", dest="output_dir", default="./output", help="the directory to output images")

    return parser.parse_args()

def compute_alpha(scribble: np.ndarray):
    '''
        根据草图计算 alpha 向量
        Args: 
            scribble: 草图数据，channels=4
    '''
    transparent = scribble[:, :, 3] ## 透明度通道
    transparent = transparent.flatten()
    gray = (scribble[: ,: , 0] / 3+ scribble[: ,: , 1] / 3+ scribble[: ,: , 2] / 3) ## 灰度值

    gray = gray.flatten()

    constraint = transparent > 0 ## 透明度大于 0 的像素为约束像素
    foreground = np.logical_and(gray > 128, constraint) ## 灰度值大于 128 的约束像素为前景约束
    background = np.logical_and(gray < 128, constraint) ## 灰度值小于 128 的约束像素为背景约束

    alpha = np.zeros(len(transparent))
    alpha[foreground] = 255
    alpha[background] = 0
    unknown = np.logical_not(foreground | background)
    alpha[unknown] = 128
    return alpha

if __name__ == '__main__':
    
    args = arg_parse()

    image_path = args.image
    trimap_path = args.trimap
    scribble_path = args.scribble
    mode = args.mode
    output_dir = "./output"

    matting=CloseFormMatting()

    source = cv2.imread(image_path)
    filename=os.path.basename(os.path.splitext(image_path)[0])

    if mode == "matting":
        if trimap_path is not None:
            trimap = cv2.imread(trimap_path, 0)
            alpha = trimap.flatten() / 255.0
            
        elif scribble_path is not None:
            scribble = cv2.imread(scribble_path, cv2.IMREAD_UNCHANGED)
            alpha=compute_alpha(scribble) / 255
        else:
            print("Error: trimap_path and scribble_path can not all be None.")
            sys.exit(1)

        gray, mat = matting.run(source, alpha)
        cv2.imwrite(f"{output_dir}/alpha/{filename}.png", gray)
        cv2.imwrite(f"{output_dir}/matting/{filename}.png", mat)
    elif mode == "eigen":
        ev0, ev1 = matting.eigen_image(source)
        cv2.imwrite(f"{output_dir}/eigen/{filename}.jpg", ev0)
        cv2.imwrite(f"{output_dir}/eigen/{filename}.jpg", ev1)