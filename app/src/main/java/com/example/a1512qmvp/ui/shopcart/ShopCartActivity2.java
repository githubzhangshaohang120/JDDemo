package com.example.a1512qmvp.ui.shopcart;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.a1512qmvp.R;
import com.example.a1512qmvp.bean.GetCartsBean;
import com.example.a1512qmvp.bean.SellerBean;
import com.example.a1512qmvp.component.DaggerHttpComponent;
import com.example.a1512qmvp.ui.base.BaseActivity;
import com.example.a1512qmvp.ui.shopcart.adapter.ElvShopcartAdapter2;
import com.example.a1512qmvp.ui.shopcart.contract.ShopcartContract;
import com.example.a1512qmvp.ui.shopcart.presenter.ShopcartPresenter;
import com.example.a1512qmvp.utils.DialogUtil;

import java.util.List;

public class ShopCartActivity2 extends BaseActivity<ShopcartPresenter> implements ShopcartContract.View {

    private ExpandableListView mElv;
    /**
     * 全选
     */
    private CheckBox mCbAll;
    /**
     * 合计：
     */
    private TextView mTvMoney;
    /**
     * 去结算：
     */
    private TextView mTvTotal;
    private ProgressDialog progressDialog;
    private ElvShopcartAdapter2 adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        //请求购物车接口
        mPresenter.getCarts(getUid(), getToken());
        progressDialog = DialogUtil.getProgressDialog(this);
    }

    private void initView() {
        mElv = (ExpandableListView) findViewById(R.id.elv);
        mCbAll = (CheckBox) findViewById(R.id.cbAll);
        mTvMoney = (TextView) findViewById(R.id.tvMoney);
        mTvTotal = (TextView) findViewById(R.id.tvTotal);

        mCbAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter != null) {
                    adapter.changAllState(mCbAll.isChecked());
                }
            }
        });
    }

    @Override
    public int getContentLayout() {
        return R.layout.activity_shop_cart2;
    }

    @Override
    public void inject() {
        DaggerHttpComponent.builder()
                .build()
                .inject(this);
    }

    /**
     * 获取购物车列表
     *
     * @param groupList
     * @param childList
     */
    @Override
    public void showCartList(List<SellerBean> groupList, List<List<GetCartsBean.DataBean.ListBean>> childList) {
        adapter = new ElvShopcartAdapter2(this, groupList, childList, progressDialog,
                mPresenter);
        //判断所有商家是否都选中，如果都选中则把全选设置为true
        if (adapter != null) {
            mCbAll.setChecked(adapter.isAllSelected());
        }

        mElv.setAdapter(adapter);
        //展开列表
        for (int i = 0; i < groupList.size(); i++) {
            mElv.expandGroup(i);
        }

        if (adapter != null) {
            String[] moneyAndCount = adapter.getMoneyAndCount();
            mTvMoney.setText("总计：" + moneyAndCount[0] + "元");
            mTvTotal.setText("总共(" + moneyAndCount[1] + ")个");
        }
    }

    /**
     * 更新成功
     *
     * @param msg
     */
    @Override
    public void updateCartsSuccess(String msg) {
        if (adapter != null) {
            adapter.updateSuceess();
        }
    }

    @Override
    public void deleteCartSuccess(String msg) {

    }
}
