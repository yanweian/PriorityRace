package com.yan.priorityrace;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Yan on 2016/11/17.
 */

public class MyAdapter extends BaseAdapter {
    private ArrayList<PCB> pcbs;
    private LayoutInflater inflater;

    public MyAdapter(Context contexts,ArrayList<PCB> pcbs) {
        inflater=LayoutInflater.from(contexts);
        this.pcbs = pcbs;
    }

    @Override
    public int getCount() {
        return pcbs.size();
    }

    @Override
    public Object getItem(int position) {
        return pcbs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.itemlayout,null);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        PCB pcb=pcbs.get(position);
        viewHolder.id.setText("进程id: "+pcb.id);
        viewHolder.progress.setText("剩余进度: "+pcb.progress);
        viewHolder.priority.setText("优先级: "+pcb.priority);
        return convertView;
    }

    class ViewHolder{
        public TextView id;
        public TextView priority;
        public TextView progress;

        public ViewHolder(View v){
            id= (TextView) v.findViewById(R.id.aid);
            priority=(TextView)v.findViewById(R.id.apriority);
            progress= (TextView) v.findViewById(R.id.aprogress);
        }
    }
}
