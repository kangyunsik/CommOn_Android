package com.example.test;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class FriendsOpenViewAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<FriendsViewItem> viewItems = new ArrayList<FriendsViewItem>();
    private ArrayList<Boolean> selected;

    // ListViewAdapter의 생성자
    public FriendsOpenViewAdapter() {
        selected = new ArrayList<>();
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return viewItems.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_friends_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView);
        TextView idTextView = (TextView) convertView.findViewById(R.id.id);
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);


        if(selected.get(pos)){
            checkBox.setChecked(true);
        }else
            checkBox.setChecked(false);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        FriendsViewItem friendsViewItem = viewItems.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        iconImageView.setImageDrawable(friendsViewItem.getIcon());
        idTextView.setText(friendsViewItem.getId());

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selected.set(position, true);
                } else {
                    selected.set(position, false);
                }
            }
        });

        //((CheckBox)convertView.findViewById(R.id.checkBox)).setChecked(((ListView)parent).isItemChecked(position));
        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return viewItems.get(position);
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(Drawable icon, String id, boolean reveal) {
        FriendsViewItem item = new FriendsViewItem();
        if (reveal) {
            item.setChecked(true);
            selected.add(true);
        }
        else {
            item.setChecked(false);
            selected.add(false);
        }

        item.setIcon(icon);
        item.setId(id);

        viewItems.add(item);
    }

    public boolean isSelected(int position) {
        return selected.get(position);
    }
}