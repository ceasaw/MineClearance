package com.hellotheworld.mineclearance;

import android.util.Log;

import java.util.Random;

public class MineZone {

    public final static int EMPTY = 0;
    public final static int MINE = -1;

    public MineZone()
    {
        //默认初级的x，y
        iColumn = 10;
        iRow = 12;
        iMineCount = 20;
        aZone = new int[iColumn][iRow];
        aOpen = new boolean[iColumn][iRow];
        aMarked = new boolean[iColumn][iRow];

        InitMineZone();
    }

    /**
     * @param row 行数
     * @param column 列数
     * @param mineCount 雷的数目
     */
    public MineZone(int row, int column, int mineCount)
    {
        iColumn = column;
        iRow = row;
        iMineCount = mineCount;
        aZone = new int[iColumn][iRow];
        aOpen = new boolean[iColumn][iRow];
        aMarked = new boolean[iColumn][iRow];

        InitMineZone();
    }

    private int[][] aZone;

    /**
     * 得到雷区的函数，在调用翻开后需要重新获得雷区
     */
    public int[][] getMineZone()
    {
        return this.aZone;
    }
    public boolean[][] getOpenZone() { return this.aOpen; }

    private boolean[][] aOpen;

    //Todo: 增加了标记数组，需要修改原来的文档
    private boolean[][] aMarked;

    private int iColumn; //列数，x值
    private int iRow; //行数，y值
    private int iMineCount; //雷的总数

    private int maxOffSet = 2; //检测区域从[-1,1]


    /**
     * 初始化雷区
     */
    private void InitMineZone()
    {
        int x, y = 0;

        //创建随机器
        long curTime = System.currentTimeMillis();
        Random rand = new Random(curTime);

        for(int i = 0; i < iMineCount; i++)
        {
            x = rand.nextInt(iColumn); //生成的随机数不包括上界[0, iColumn)
            y = rand.nextInt(iRow);
            while(aZone[x][y] == MINE)
            {
                x = rand.nextInt(iColumn); //生成的随机数不包括上界[0, iColumn)
                y = rand.nextInt(iRow);
            }
            aZone[x][y] = MINE;
        }
        FillMineZone();
    }

    /**
     * 在雷区初始化后进行数字填充
     */
    private void FillMineZone()
    {

        //对每个方块都进行四周8个块的查询
        for(int x = 0; x < iColumn; x++)
        {
            for(int y = 0; y < iRow; y++)
            {
                //非空区域就不进行填充
                if(aZone[x][y] != EMPTY)
                    continue;

                //对周围包括自己在内的9格区域进行检索
                for(int i = -1; i < maxOffSet; i++)
                {
                    for(int j = -1; j < maxOffSet; j++)
                    {
                        if(x + i >= 0 && x + i < iColumn && y + j >= 0 && y + j < iRow)
                        {
                            if(aZone[x + i][y + j] == MINE)
                                aZone[x][y]++;
                        }
                    }
                }
            }
        }
    }


    //Todo: 增加了标记函数，需要修改原来的文档
    /**
     * 标记区域防止被开启
     */
    public void MarkZone(int x, int y)
    {
        //防止标记越界
        if(x < 0 || x > iColumn || y < 0 || y > iRow)
        {
            Log.i("MarkError", "MarkZone: Index out of range");
            return;
        }

        //处理标记已经翻开的区域
        if(aOpen[x][y] == true)
        {
            Log.i("MarkError", "MarkZone: The zone has been opened!");
            return;
        }

        //直接取反
        aMarked[x][y] = !aMarked[x][y];
    }

    /**
     * 递归调用开启一片区域
     */
    public void OpenZone(int x, int y)
    {
        //已经翻开的区域就直接返回
        if(aOpen[x][y] == true)
            return;

        //已经标记的区域直接返回
        if(aMarked[x][y] == true)
            return;

        //处理待翻开区域是雷的情况
        if(aZone[x][y] == MINE)
            return;

        //处理待翻开区域是空白的情况
        if(aZone[x][y] == EMPTY)
        {
            //翻开，然后递归包括自己在内的周围的9个位置
            aOpen[x][y] = true;
            for(int i = -1; i < maxOffSet; i++)
            {
                for(int j = -1; j < maxOffSet; j++)
                {
                    if(x + i >= 0 && x + i < iColumn && y + j >= 0 && y + j < iRow)
                    {
                        //自己递归自己已经在前面处理过（已经翻开）
                        OpenZone(x + i, y + j);
                    }
                }
            }
        }

        //处理待翻开区域是数字的情况
        if(aZone[x][y] != MINE && aZone[x][y] != EMPTY)
        {
            aOpen[x][y] = true;
        }
    }

}
