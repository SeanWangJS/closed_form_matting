package com.haswalk.matting;

import org.junit.Test;

public class MattingTest {

    @Test
    public void test() {

        String sourcePath = MattingTest.class.getResource("/source.jpg").getPath();
        String trimapPath = MattingTest.class.getResource("/trimap.jpg").getPath();
        String resultPath = sourcePath.substring(0, sourcePath.lastIndexOf("/")) + "/result.png";

        ClosedFormMatting.run(sourcePath, trimapPath, resultPath);

    }

}
