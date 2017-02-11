package com.ramo.fragment;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.ramo.adapter.SearchResultAdapter;
import com.ramo.bean.File;
import com.ramo.file_transfer.R;
import com.ramo.utils.SearchFileUtil;
import com.ramo.utils.T;
import com.ramo.view.WPStyleView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramo on 2016/3/10.
 */
@EFragment(R.layout.send_all_file)
public class AllFileClassificationFragment extends Fragment {

    @ViewById
    EditText queryFileText;
    @ViewById
    ImageButton queryFileExit;
    @ViewById
    WPStyleView file_type_statistical_view;
    @ViewById
    RecyclerView search_file_result_rv;

    @ViewById
    ImageView load_search_result_gifview;

    SearchResultAdapter searchFileResultAdapter;
    List<File> resultList;

    SearchFileUtil searchFileUtil;
    Animation searchAnimation;

    @AfterViews
    public void init() {
        resultList = new ArrayList<File>();
        searchFileResultAdapter = new SearchResultAdapter(resultList);
        search_file_result_rv.setAdapter(searchFileResultAdapter);
        search_file_result_rv.setLayoutManager(new LinearLayoutManager(getContext()));
        searchFileUtil = new SearchFileUtil(getContext());

        searchAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.search_anim);
        initListener();
    }

    private void initListener() {
        queryFileText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    queryFileExit.setVisibility(View.GONE);
                } else {
                    queryFileExit.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                doAfterTextChanged();
            }
        });
    }

    private void doAfterTextChanged() {
        resultList.clear();
        searchFileResultAdapter.notifyDataSetChanged();

        if (TextUtils.isEmpty(queryFileText.getText().toString())) {
            resultList.clear();
            searchFileResultAdapter.notifyDataSetChanged();

            search_file_result_rv.setVisibility(View.GONE);
            file_type_statistical_view.setVisibility(View.VISIBLE);
        } else {
            searchThread();
            search_file_result_rv.setVisibility(View.VISIBLE);
            file_type_statistical_view.setVisibility(View.GONE);
        }

    }

    private void searchThread() {
        SearchAsyncTask task = new SearchAsyncTask();

        if (task != null && task.getStatus() == AsyncTask.Status.RUNNING)
            task.cancel(true);
        task.execute();
    }

    @Click(R.id.queryFileExit)
    public void queryFileExitClickEvent() {
        queryFileText.setText("");
    }


    public class SearchAsyncTask extends AsyncTask<String, Void, Void> {
        List<File> fileList;

        @Override
        protected Void doInBackground(String... params) {
            if (!isCancelled()) {
                fileList = searchFileUtil.browserFile(queryFileText.getText().toString());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            load_search_result_gifview.setVisibility(View.VISIBLE);
            load_search_result_gifview.startAnimation(searchAnimation);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            load_search_result_gifview.setVisibility(View.GONE);
            load_search_result_gifview.clearAnimation();
            if (fileList == null || fileList.size() == 0)
                T.showShort(getContext(), getContext().getString(R.string.notFound));
            else
                for (File f : fileList) {
                    resultList.add(f);
                }
            searchFileResultAdapter.notifyDataSetChanged();
        }

    }

}
