package com.hash.coinconvert.ui2.fragment;

import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.hash.coinconvert.databinding.ActivityGameBinding;
import com.hash.coinconvert.ui2.activity.BaseActivity;
import com.hash.coinconvert.utils.StatusBarUtils;
import com.hash.coinconvert.vm.GameViewModel;
import com.hash.coinconvert.widget.wheelview.RotateListener;
import com.hash.coinconvert.widget.wheelview.WheelSurfView;

import java.util.Random;

public class GameActivity extends BaseActivity {
    private ActivityGameBinding binding;
    private GameViewModel viewModel;

    public GameActivity() {
        super();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(GameViewModel.class);
        viewModel.pinCheck();
        viewModel.pinList();
        binding = ActivityGameBinding.inflate(getLayoutInflater());

        StatusBarUtils.setViewHeightEqualsStatusBarHeight(binding.paddingView);
        setContentView(binding.getRoot());

        binding.ivActionBack.setOnClickListener(v -> finish());

        //获取第二个视图
        final WheelSurfView wheelSurfView1 = binding.wheelSurfView1;
        //添加滚动监听
        wheelSurfView1.setRotateListener(new RotateListener() {
            @Override
            public void rotateEnd(int position, String des) {
                Toast.makeText(GameActivity.this, "结束了 位置：" + position + "   描述：" + des, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void rotating(ValueAnimator valueAnimator) {

            }

            @Override
            public void rotateBefore(ImageView goImg) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
                builder.setTitle("温馨提示");
                builder.setMessage("确定要花费100积分抽奖？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //模拟位置
                        int position = new Random().nextInt(7) + 1;
                        wheelSurfView1.startRotate(position);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();

            }
        });

//        binding.imageButtonStart.setEnabled(false);
        binding.imageButtonStart.setOnClickListener(v -> {
            int position = new Random().nextInt(7) + 1;
            wheelSurfView1.startRotate(position);
        });
    }
}
