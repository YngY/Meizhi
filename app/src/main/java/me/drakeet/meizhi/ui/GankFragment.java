package me.drakeet.meizhi.ui;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.meizhi.R;
import me.drakeet.meizhi.adapter.GankListAdapter;
import me.drakeet.meizhi.data.GankData;
import me.drakeet.meizhi.model.Gank;
import me.drakeet.meizhi.ui.base.BaseActivity;
import me.drakeet.meizhi.ui.base.SwipeRefreshFragment;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by drakeet on 8/11/15.
 */
public class GankFragment extends SwipeRefreshFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    RecyclerView mRecyclerView;
    List<Gank> mGankList;
    GankListAdapter mAdapter;
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    float mRvY;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static GankFragment newInstance(int sectionNumber) {
        GankFragment fragment = new GankFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public GankFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGankList = new ArrayList<>();
        mAdapter = new GankListAdapter(mGankList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gank, container, false);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
        initRecyclerView(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.postDelayed(() -> setRefreshing(true), 300);
        getData();
        mSwipeRefreshLayout.setCanChildScrollUpCallback(() -> mRecyclerView.getY() == mRvY);
    }

    private void initRecyclerView(View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_gank);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRvY = mRecyclerView.getY();
    }

    private void getData() {
        BaseActivity.sDrakeet.getGankData(2015, 8, 11)
                .observeOn(AndroidSchedulers.mainThread())
                .map(data -> data.results)
                .map(this::addAllResults)
                .subscribe(
                        list -> {
                            mAdapter.notifyDataSetChanged();
                            setRefreshing(false);
                        }, Throwable::printStackTrace

                );
    }

    @Override
    public void requestDataRefresh() {
        super.requestDataRefresh();
        setRefreshing(false);
    }

    private Observable<List<Gank>> addAllResults(GankData.Result results) {
        mGankList.addAll(results.androidList);
        mGankList.addAll(results.iOSList);
        mGankList.addAll(results.拓展资源List);
        if (results.瞎推荐List != null)mGankList.addAll(results.瞎推荐List);
        return Observable.just(mGankList);
    }

}
