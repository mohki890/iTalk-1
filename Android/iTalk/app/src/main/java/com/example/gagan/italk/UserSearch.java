package com.example.gagan.italk;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by gagan on 14/7/15.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class UserSearch extends DialogFragment implements AdapterView.OnItemClickListener, TextWatcher {
    private TextView autoCompleteTextView;
    private ListView listView;
    private CustomSearch adapter;
    List<ElementDesc> listInfo=new ArrayList<ElementDesc>();
    private UserRoom userRoom;


    public void setUserRoom(UserRoom userRoom) {
        this.userRoom = userRoom;
    }

    void addDataToList()
    {
        for(int i=0;i<AppToServer.getAllUserNname().size();i++) {
            int id=AppToServer.getAllUserId().get(i);
            int pos=UserRoom.arrayAdapter.getPositionById(id);
            if(pos>=0)
                listInfo.add(new ElementDesc(AppToServer.getAllUserUname().get(i), AppToServer.getAllUserNname().get(i), UserRoom.arrayAdapter.getItem(pos).getDp() ,AppToServer.getAllUserId().get(i)));
            else
                listInfo.add(new ElementDesc(AppToServer.getAllUserUname().get(i), AppToServer.getAllUserNname().get(i), AppToServer.getAllUserId().get(i)));

        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        adapter=new CustomSearch(getActivity(),R.layout.userelement_search,this);
        listView=new ListView(getActivity());
        addDataToList();
        autoCompleteTextView=new AutoCompleteTextView(getActivity());
        autoCompleteTextView.setSingleLine();
        listView.setAdapter(adapter);
        getDialog().setTitle("Search Users");
        autoCompleteTextView.setHint("Username/Nickname Or Part of it");
        listView.setOnItemClickListener(this);
        autoCompleteTextView.addTextChangedListener(this);

        autoCompleteTextView.setText("");
        LinearLayout layout=new LinearLayout(getActivity());
        layout.addView(autoCompleteTextView);
        layout.addView(listView);
        layout.setOrientation(LinearLayout.VERTICAL);
        return layout;

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dismiss();
        userRoom.onSearchResult(adapter.getItem(position).id);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s.toString().isEmpty()) {adapter.clear();return;}
        MyFilter filter= (MyFilter) adapter.getFilter();
        filter.filter(s.toString());

    }
}
class ElementDesc
{
    String uname;
    String nname;
    int id;
    Drawable image;
    int match;
    ElementDesc(String uname,String nname,int id)
    {
        this.uname=uname;
        this.nname=nname;
        this.id=id;
        match=0;
    }
    ElementDesc(String uname,String nname,Drawable drawable,int id)
    {
        this.uname=uname;
        this.nname=nname;
        match=0;
        image=drawable;
        this.id=id;
    }
}
class CustomSearch extends ArrayAdapter<ElementDesc> implements Comparator<ElementDesc> {
    LayoutInflater inflater;
    Context context;
    UserSearch userSearch;
    public CustomSearch(Context context,int res,UserSearch userSearch)
    {
        super(context,res);
        this.context=context;
        this.userSearch=userSearch;
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    MyFilter filter=new MyFilter(this);


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null)
            convertView=inflater.inflate(R.layout.userelement_search,parent,false);

        ElementDesc desc=getItem(position);
        ((TextView)convertView.findViewById(R.id.ues_uname)).setText(desc.uname);
        ((TextView)convertView.findViewById(R.id.ues_nname)).setText(desc.nname);

        if(desc.image!=null)
            ((ImageView)convertView.findViewById(R.id.ues_dp)).setImageDrawable(desc.image);
        else
            ((ImageView)convertView.findViewById(R.id.ues_dp)).setImageResource(R.drawable.no_dp);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public int compare(ElementDesc lhs, ElementDesc rhs) {
        return lhs.match-rhs.match;
    }
}

class MyFilter extends Filter
{
        CustomSearch customSearch;
         MyFilter(CustomSearch customSearch)
         {
             this.customSearch=customSearch;
         }
        int Match(ElementDesc a,CharSequence cons)
        {
            String constraint=cons.toString().toLowerCase();
            String uname=a.uname.toLowerCase(),nname=a.nname.toLowerCase();
            if(uname.contains(constraint))
                return 2;
            if(nname.contains(constraint))
                return 1;
            return 0;
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
        return ((ElementDesc)resultValue).uname;
    }

        @Override
        public FilterResults performFiltering(CharSequence constraint) {

        FilterResults results=new FilterResults();
        int x;
        List<ElementDesc> list=new ArrayList<ElementDesc>();
        ElementDesc desc;
        for(int i=0;i<customSearch.userSearch.listInfo.size();i++)
        {
            if((x=Match((desc=customSearch.userSearch.listInfo.get(i)),constraint))>0)
            {
                desc.match=x;
                list.add(desc);
            }
        }

        results.values=list;

        return results;
    }


        @Override
        public void publishResults(CharSequence constraint, FilterResults results) {
        if (results.values != null) {

            customSearch.clear();
            List<ElementDesc> list = (List<ElementDesc>) results.values;

            for (int i = 0; i < list.size(); i++)
                customSearch.add(list.get(i));
            customSearch.sort(customSearch);
            customSearch.notifyDataSetChanged();
        }
    }


}

