package com.yan.priorityrace;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;

public class MainActivity extends AppCompatActivity {

    private Button button;

    private ArrayList<PCB> arrayList;
    private MyAdapter myAdapter;
    private ListView listView;
    private Queue<PCB> pcbs;//就绪队列
    private PCB nowpcb;//cpu正在执行的进程
    private boolean cpu = false;//cpu是否正在被使用,true为正在使用
    private boolean flag = true;
    private boolean pauseflag = false;
    private int count = 0;

    private TextView cputext, idtext, prioritytext, nowprogresstext;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cputext = (TextView) findViewById(R.id.cpu);
        idtext = (TextView) findViewById(R.id.processid);
        prioritytext = (TextView) findViewById(R.id.priority);
        nowprogresstext = (TextView) findViewById(R.id.nowprogress);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        pcbs = new PriorityBlockingQueue<>();
        arrayList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listview);
        myAdapter = new MyAdapter(this, arrayList);
        listView.setAdapter(myAdapter);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pcbs.size() >= 8) {
                    //就绪队列最多只有8个进程
                } else {
                    //产生新的进程
                    PCB pcb = new PCB();
                    count++;
                    pcb.id = count + "";
                    pcb.progress = 100;
                    //优先级为5以内随机数
                    pcb.priority = new Random().nextInt(baseint);
                    process(pcb);
                    baseint--;
                    if(baseint==2){
                        baseint=8;
                    }
                }
            }
        });
    }

    private int baseint = 8;


    //点击按钮，添加进程
    //参数newpcb为将要添加的进程
    private void process(final PCB newpcb) {
        //现将正在执行的线程暂停
        pauseflag = true;
        if (cpu) {
            //若cpu正在被占用
            //取得新线程的优先级
            int newpriority = newpcb.priority;
            //取得旧线程的优先级
            int oldpriority = nowpcb.priority;
            Log.i("test", "old:" + oldpriority + " new:" + newpriority);
            //比较优先级
            if (newpriority < oldpriority) {
                //若新进程优先级较高，则抢占
                /*1.停止就得线程
                * 2.将旧的进程放入就绪队列
                * 3.将新的进程，加入执行*/
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("新进程     "+newpcb.id +"  优先级："+newpcb.priority+"\n\n" +
                        "正在执行进程"+nowpcb.id+"   优先级: "+nowpcb.priority+"\n\n" +
                        "正在抢占.....");
                builder.setCancelable(false);
                final AlertDialog alertDialog=builder.create();
                alertDialog.show();
                pauseflag=false;
                flag = false;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //等待正在执行的线程停止
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        startthread();
                        alertDialog.dismiss();
                    }
                }).start();
                pcbs.add(nowpcb);
                nowpcb = newpcb;
            } else {
                //将暂停的线程启动
                pauseflag = false;
                //若优先级较低
                pcbs.add(newpcb);
            }

            arrayList.clear();
            Iterator<PCB> iterator = pcbs.iterator();
            while (iterator.hasNext()) {
                PCB pcb = iterator.next();
                Log.i("test", "id: " + pcb.id + "  priority: " + pcb.priority);
                arrayList.add(pcb);
            }
            myAdapter.notifyDataSetChanged();

        } else {
            //cpu空闲
            cpu = true;
            nowpcb = newpcb;
            startthread();
        }
    }

    public void startthread() {
        flag = true;
        pauseflag = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                idtext.setText("进程id: " + nowpcb.id);
                prioritytext.setText("优先级: " + nowpcb.priority);
                cputext.setText("cpu状态: 正在被占用...");
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                int progress = nowpcb.progress;
                while (flag) {
                    if (progress == 0) {
                        //从队列中拿出一个
                        /*1.如果队列为空，则停止
                          2.如果队列不空，则从队列中取出一个
                         */
                        if (pcbs.isEmpty()) {
                            cpu = false;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cputext.setText("cpu状态: 空闲");
                                }
                            });
                        } else {
                            nowpcb = pcbs.poll();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    arrayList.clear();
                                    Iterator<PCB> iterator = pcbs.iterator();
                                    while (iterator.hasNext()) {
                                        PCB pcb = iterator.next();
                                        Log.i("test", "id: " + pcb.id + "  priority: " + pcb.priority);
                                        arrayList.add(pcb);
                                    }
                                    myAdapter.notifyDataSetChanged();
                                }
                            });
                            //开始执行线程
                            startthread();
                        }
                        break;
                    }

                    while (pauseflag) {
                        //暂停
                    }

                    progress--;
                    nowpcb.progress = progress;
                    //改变界面
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int nowprogress = 100 - nowpcb.progress;
                            nowprogresstext.setText(nowprogress + "");
                            progressBar.setProgress(nowprogress);
                        }
                    });
                    Log.i("test", nowpcb.id + " progress" + progress);
                    //200ms
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
