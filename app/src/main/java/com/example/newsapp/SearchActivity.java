package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class SearchActivity extends AppCompatActivity{

    private int ID;
    private User user;
    private Context mContext;
    private NewsCollection news;
    private ArrayList<String> keyword;
    private SearchView mSearchView;
    private ListView mListView;
    private SearchAdapter mAdapter;


    @Override
    protected void onCreate(Bundle before){
        super.onCreate(before);
        setContentView(R.layout.activity_search);
        mContext = SearchActivity.this;
        final Intent intent = getIntent();
        ID = intent.getExtras().getInt("ID");
        user = new User();
        user.setID(ID);

        keyword = (ArrayList<String>) user.GetSearchHistory();
        //System.out.println(keyword.get(0));
        if(keyword==null){
            keyword = new ArrayList<String>();
        }

        mSearchView = (SearchView) findViewById(R.id.search_view);
        mListView = (ListView) findViewById(R.id.search_list_view);

        //mAdapter = new ArrayAdapter<String>(this,R.id.text_search,keyword);
        mAdapter = new SearchAdapter(keyword,mContext,ID,mSearchView);

        mListView.setAdapter(mAdapter);
        mListView.setTextFilterEnabled(true);
        mListView.setOnItemClickListener(new OnClick());

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //
                Intent newintent = new Intent(SearchActivity.this,HistoryActivity.class);
                newintent.putExtra("isForYou",2);
                newintent.putExtra("key",s);
                user.SaveSearchHistory(s);
                startActivity(newintent);
                if(!keyword.contains(s)){
                    keyword.add(s);
                    mAdapter.notifyDataSetChanged();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(!TextUtils.isEmpty(s)){
                    mAdapter.setFilterText(s);
                }else{
                    mAdapter.clearTextFilter();
                }
                return false;
            }
        });
    }

    class OnClick implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String text = ((TextView) view).getText().toString();
            mSearchView.setQuery(text,false);
        }
    }
}