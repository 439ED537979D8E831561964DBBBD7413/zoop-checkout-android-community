package com.zoop.checkout.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mainente on 29/04/15.
 */
public class ExpandableAdapter extends BaseExpandableListAdapter {
    private List<String> listGroup;
    private HashMap<String, List<String>> listData;
    private LayoutInflater inflater;
    public ArrayList<Integer> icon;


    public ExpandableAdapter(Context context, List<String> listGroup, HashMap<String, List<String>> listData,ArrayList<Integer> icon){
        this.listGroup = listGroup;
        this.listData = listData;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.icon=icon;
    }

    @Override
    public int getGroupCount() {
        return listGroup.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listData.get(listGroup.get(groupPosition)).size();
    }


    @Override
    public Object getGroup(int groupPosition) {
        return listGroup.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listData.get(listGroup.get(groupPosition)).get(childPosition);
    }




    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolderGroup holder;
        ViewHolderIcon holderi;
        holderi=new ViewHolderIcon();



        if(convertView == null){
            convertView = inflater.inflate(R.layout.listoptions, null);
            holder = new ViewHolderGroup();
            convertView.setTag(holder);

            holder.tvGroup = (TextView) convertView.findViewById(R.id.tvGroup);
        }
        else{
            holder = (ViewHolderGroup) convertView.getTag();
        }

        holder.tvGroup.setText(listGroup.get(groupPosition));
         holderi.imgIcone      = (ImageView) convertView.findViewById(R.id.item_icon);


        holderi.imgIcone  .setImageResource(icon.get(groupPosition));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolderItem holder;
        ViewHolderIconchild holderi;
        holderi=new ViewHolderIconchild();
        String val = (String) getChild(groupPosition, childPosition);
       // Integer ic= (Integer) iconchild.get((listGroup.get(groupPosition))).get(childPosition);

        if(convertView == null){
            convertView = inflater.inflate(R.layout.listsuboptions, null);
            holder = new ViewHolderItem();
            convertView.setTag(holder);

            holder.tvItem = (TextView) convertView.findViewById(R.id.tvItem);
        }
        else{
            holder = (ViewHolderItem) convertView.getTag();
        }
        holderi.imgIconechild      = (ImageView) convertView.findViewById(R.id.item_iconchild);


     //   holderi.imgIconechild  .setImageResource(ic);

        holder.tvItem.setText(val);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class ViewHolderGroup {
        TextView tvGroup;
    }

    class ViewHolderItem {
        TextView tvItem;
    }
    class ViewHolderIcon{

        ImageView imgIcone;

    }

    class ViewHolderIconchild{

        ImageView imgIconechild;

    }

}
