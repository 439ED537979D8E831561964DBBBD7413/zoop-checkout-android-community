package com.zoop.checkout.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mainente on 04/05/15.
 */
public class OptionsMenu extends Activity {
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private LinearLayout menuL;
    private String[] mZoopOptions;
    private  ExpandableListView expandableListView;
    private List<String> listGroup;
    private HashMap<String, List<String>> listData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void MountOpitions(final Context C,ExpandableListView expandableListView,String[] ZoopOptions){

        mZoopOptions = ZoopOptions;


        buildList();

        this.expandableListView = expandableListView;
      //  expandableListView.setAdapter(new ExpandableAdapter(C, listGroup, listData));

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener(){
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                Toast.makeText(C, "Group: " + groupPosition + "| Item: " + childPosition, Toast.LENGTH_SHORT).show();
                //if (groupPosition==
                return false;
            }
        });

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener(){
            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition==0) {
                    Intent intent = new Intent(OptionsMenu.this, ChargeActivity.class);
                    startActivity(intent);
                }
              Toast.makeText(C, "Group (Expand): "+groupPosition, Toast.LENGTH_SHORT).show();
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener(){
            @Override
            public void onGroupCollapse(int groupPosition) {
            }
        });

    }
    public void buildList(){
        listGroup = new ArrayList<String>();
        listData = new HashMap<String, List<String>>();

        for(int i=0;i<mZoopOptions.length;i++) {

            // GROUP
            listGroup.add(mZoopOptions[i]);
        }


        // CHILDREN
        List<String> auxList = new ArrayList<String>();

        listData.put(listGroup.get(0), auxList);

        auxList = new ArrayList<String>();


        listData.put(listGroup.get(1), auxList);

        auxList = new ArrayList<String>();

        listData.put(listGroup.get(2), auxList);

        auxList = new ArrayList<String>();
        auxList.add("Ativar Métodos de pagamentos");
        auxList.add("Configurar Impressora");

        listData.put(listGroup.get(3), auxList);


        auxList = new ArrayList<String>();


        listData.put(listGroup.get(4), auxList);
        auxList = new ArrayList<String>();
        auxList.add("Perguntas Frequentes");
        auxList.add("Contato");

        listData.put(listGroup.get(5), auxList);

        auxList = new ArrayList<String>();


        listData.put(listGroup.get(6), auxList);



    }
}
