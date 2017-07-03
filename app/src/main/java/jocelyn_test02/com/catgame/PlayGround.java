package jocelyn_test02.com.catgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by Jocelyn on 27/7/2016.
 */
public class PlayGround extends SurfaceView implements View.OnTouchListener{

    //int test = 1;

    private final int Row = 10; //行
    private final int Col = 7; //列
    private final int BLOCKS = 15; //默认路障的数量

    private static int width = 40; //Dot的宽
    private static int hight = 40; //Dot的高


    SurfaceHolder holder;
//二维DDot类对象数组
   private Dot dots[][];
    private Dot cat;

    public PlayGround(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(callback);
        dots = new Dot[Row][Col];
        for(int i =0;i < Row ; i++){

            for(int j =0;j < Col ; j++){
                dots[i][j] = new Dot(j,i);
            }
        }
        //设置触摸监听
        setOnTouchListener(this);

        initGame();
    }
//反回值为一个点Dot
    private Dot getDot(int x,int y){
       if(x<Col && x > -1 && y<Row && y > -1){
           return dots[y][x];
       }else {
           return null;
       }
    }

//判断是不处在边缘的点
    private boolean isAtEdge(Dot d){
        if(d.getX()*d.getY() == 0 || d.getX()+1 == Col || d.getY()+1 == Row){
            return true;
        }
        return false;
    }

//取得每点在6个不同方向的邻居,中间的点为ctr ,dir方向，从左1.2.3..6顺时钟看
    private Dot getNeighbour(Dot ctr, int dir){
        switch(dir){
            case 1:         //left
                 return getDot(ctr.getX()-1,ctr.getY());

            case 2:         // up left
                if(ctr.getY() %2 == 0){//说明在偶数行，没有缩进
                    return getDot(ctr.getX()-1,ctr.getY()-1);
                }else {//否则在奇数行，有缩进ctr
                    return getDot(ctr.getX(),ctr.getY()-1);
                }

            case 3:         //up right
                if(ctr.getY() %2 == 0){//偶数行，没有缩进
                    return getDot(ctr.getX(),ctr.getY()-1);
                }else {//奇数行，有缩进ctr
                    return getDot(ctr.getX()+1,ctr.getY()-1);
                }

            case 4:         //right
                return getDot(ctr.getX()+1,ctr.getY());//right

            case 5://down right
                if(ctr.getY() %2 == 0){//偶数行，没有缩进
                    return getDot(ctr.getX(),ctr.getY()+1);
                }else {//奇数行，有缩进
                    return getDot(ctr.getX()+1,ctr.getY()+1);
                }

            case 6:         //down left
                if(ctr.getY() %2 == 0){//偶数行，没有缩进
                    return getDot(ctr.getX()-1,ctr.getY()+1);
                }else {//奇数行，有缩进
                    return getDot(ctr.getX(),ctr.getY()+1);
                }

            default:
                break;
        }
        return null;
    }
    //获取各方向可移动的点的距离（数量）
    private int getDistance(Dot currDot, int dir)
    {   int distance = 0;
        if(isAtEdge(currDot)){
            return 1;
        }
        Dot cur,next;
        cur = currDot;
        while(true){
           next = getNeighbour(cur,dir);
            //判断是否碰到路障
            if(next.getStatus()==Dot.STATUS_ON){
                return distance * -1 ;
            }
            //判断是否到达边缘
          //  if(getNeighbour(next,dir)==null){
            if(isAtEdge(next)){
                return distance + 1 ;
            }
            distance++;
            cur = next;
        }
    }
    //移动到下一个点
    private void moveTo(Dot target){
        target.setStatus(Dot.STATUS_IN);
        getDot(cat.getX(),cat.getY()).setStatus(Dot.STATUS_OFF);
        cat.setXY(target.getX(),target.getY());
    }
    //
    private void move(){
        if(isAtEdge(cat)){
            lose();return;
        }
        //
        Vector<Dot> available = new Vector<>(); //记录相邻可以用的点 的集合
        Vector<Dot> positive = new Vector<>();  //记录相邻可以用的点能达边缘点的集合
        HashMap<Dot,Integer> Dirs = new HashMap<Dot,Integer>(); //记录能达边缘点的方向
        for(int i=1; i<7; i++){
          Dot n = getNeighbour(cat,i);
           if(n.getStatus() == Dot.STATUS_OFF) {
               available.add(n);
               Dirs.put(n,i);
               if(getDistance(n,i) > 0){
                   positive.add(n);
               }
           }
        }

        if(available.size() == 0){
            win();
        }else if(available.size() == 1){
            moveTo(available.get(0));
        }else {
            Dot bestNb = null;
            if(positive.size() != 0)//如果存在可以达到边缘的点
            {   int min = 999;

                for(int i=0; i < positive.size();i++){
                    int temp = getDistance(positive.get(i),Dirs.get(positive.get(i)));
                    if(temp < min){
                        min = temp;
                        bestNb = positive.get(i);
                    }
                }
            }else {             //所有的方向都有路障
//            int s = (int)((Math.random()*1000) % available.size());
//            moveTo(available.get(s));
                int max = 0;
                for(int i=0; i< available.size();i++){
                    int tp = getDistance(available.get(i),Dirs.get(available.get(i)));
                    if(tp < max){
                        max = tp;
                        bestNb = available.get(i);
                    }
                }

            }
         moveTo(bestNb);
        }
    }

    private void lose(){
        Toast.makeText(getContext(),"You Lose The Game!",Toast.LENGTH_SHORT).show();
    }

    private void win(){
        Toast.makeText(getContext(),"You Win The Game!",Toast.LENGTH_SHORT).show();
    }


    private void Ondraw()
    {
        Canvas c = holder.lockCanvas();
        c.drawColor(Color.LTGRAY);
        Paint paint = new Paint();
        //设置打开反锯齿效果。.setFlags(paint.ANTI_ALIAS_FLAG);
        paint.setFlags(paint.ANTI_ALIAS_FLAG);

        for(int i =0;i < Row ; i++){
            int offset = 0;
            if(i % 2 == 1){
                offset += width/2;
            }
            for(int j =0;j < Col ; j++){
                Dot curr = getDot(j,i);
                switch (curr.getStatus()){
                    case Dot.STATUS_ON:
                        paint.setColor(0xFFFFAA00);
                        break;
                    case Dot.STATUS_OFF:
                        paint.setColor(0xFFEEEEEE);
                        break;
                    case Dot.STATUS_IN:
                        paint.setColor(0xFFFF0000);
                        break;
                    default:
                        break;
                }
                c.drawOval(curr.getX()*width+offset,curr.getY()*hight,
                        (curr.getX()+1)*width+offset,(curr.getY()+1)*hight,paint);
            }
        }

        holder.unlockCanvasAndPost(c);
    }

    SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder sH) {
            Ondraw();
        }

        @Override
        public void surfaceChanged(SurfaceHolder sH, int i, int i1, int i2) {
            width = i1/(Col+1);
            hight = width;
            Ondraw();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder sH) {

        }
    };
//初始游戏
    private void initGame(){
        for(int i =0;i < Row ; i++){

            for(int j =0;j < Col ; j++){

                dots[i][j].setStatus(Dot.STATUS_OFF);
            }
        }
        cat = new Dot(4,5);
        getDot(4,5).setStatus(Dot.STATUS_IN);

        for(int i =0;i < BLOCKS; ){
            int x =(int) (Math.random() *100 % Col);
            int y =(int) (Math.random() *100 % Row);

            if(getDot(x,y).getStatus() == Dot.STATUS_OFF){
                getDot(x,y).setStatus(Dot.STATUS_ON);
                i++;
                System.out.println("BLOCKS:"+i);
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent me) {
        //MotionEvent.ACTION_UP 手指触摸 释放.ACTION_UP
        if (me.getAction() == MotionEvent.ACTION_UP) {
//            Toast.makeText(getContext(),me.getX()+":"+me.getY(),
//                    Toast.LENGTH_SHORT).show();

            //这里的x,y算出的是行和列+1才是真正的第几个
            int x, y;
            y = (int) (me.getY() / width);
            //判断是否缩进行
            if (y % 2 == 0) {
                x = (int) (me.getX() / width);
            } else {
                x = (int) ((me.getX() - width / 2) / width);
            }
            //判断点击是否有在DOTS中的有效位，不然数组越界 程序会退出
            if ((x + 1) > Col || (y + 1) > Row) {
                //重新刷新画面
                initGame();
                // return false;点击无效处理
                // getNeighbour(cat,test).setStatus(Dot.STATUS_IN);
                // test++;
                // Ondraw();
//                System.out.println("-----------------------");
//                for(int i =1;i <7; i++){
//                    System.out.println(i+"@"+getDistance(cat,i));
//                }

            } else if (getDot(x, y).getStatus() == Dot.STATUS_OFF) {
                //被点的DOT 状态 变成（不可用）的路障模式
                getDot(x, y).setStatus(Dot.STATUS_ON);
                move();
            }

            Ondraw();
        }
        return true;
    }

}
