package com.xframe.widget.fileSelection;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.xframe.widget.databinding.DialogFileSelectionHomeBinding;
import com.xframe.widget.fileSelection.Adapter.FileSelectionAdapter;
import com.xframe.widget.recycler.RecyclerItemClickListener;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FileSelectionDialog extends Dialog implements View.OnClickListener ,RecyclerItemClickListener.OnItemClickListener.Normal{
    private DialogFileSelectionHomeBinding binding;
    private final File curDirectory = new File(Environment.getExternalStorageDirectory().getPath());
    private final List<File> lists = new ArrayList<>();
    private OnFileSelection fileSelection;
    //当前页面
    private File file = curDirectory;
    private FileSelectionAdapter adapter;

    public FileSelectionDialog(@NonNull Context context) {
        super(context);
        initData(curDirectory);
        initDialog();
        initView(context);
        setContentView(binding.getRoot());
        //设置window背景，默认的背景会有Padding值，不能全屏。当然不一定要是透明，你可以设置其他背景，替换默认的背景即可。
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //一定要在setContentView之后调用，否则无效
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }


    public void setFileSelection(OnFileSelection fileSelection) {
        this.fileSelection = fileSelection;
    }

    private void initData(File filE) {
        this.file = filE;
        File[] files = filE.listFiles();
        //文件集合
        List<File> Files = new ArrayList<>();
        //文件夹
        List<File> folder = new ArrayList<>();
        if (files == null) {
            Toast.makeText(getContext(), "无权限!", Toast.LENGTH_SHORT).show();
            return;
        }
        lists.clear();
        for (File file : files) {
            if (file.isFile()) {
                //文件
                Files.add(file);
            } else {
                //文件夹
                folder.add(file);
            }
        }
        folder.sort((v1, v2) -> Collator.getInstance(Locale.CHINA).compare(v1.getName(), v2.getName()));
        Files.sort((v1, v2) -> Collator.getInstance(Locale.CHINA).compare(v1.getName(), v2.getName()));
        lists.addAll(folder);
        lists.addAll(Files);
    }

    /**
     * @param file 返回
     */
    @SuppressLint("NotifyDataSetChanged")
    private void upper(File file) {
        String text = file.getAbsolutePath();
        text = text.substring(0, text.lastIndexOf("/"));
        File file1 = new File(text);
        initData(file1);
        adapter.notifyDataSetChanged();
    }


    private void initView(Context context) {
        binding = DialogFileSelectionHomeBinding.inflate(LayoutInflater.from(context));
        binding.toolbar.setNavigationOnClickListener(v -> dismiss());
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        binding.recycle.addOnItemTouchListener(new RecyclerItemClickListener(context, this));
        binding.recycle.setLayoutManager(layoutManager);
        adapter = new FileSelectionAdapter(lists);
        binding.recycle.setAdapter(adapter);

        binding.upper.getRoot().setOnClickListener(v -> upper(file));
    }

    private void initDialog() {
        Window window = getWindow();
        WindowManager.LayoutParams dialog_window_attributes = window.getAttributes();
        dialog_window_attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog_window_attributes.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(dialog_window_attributes);
        setCancelable(false);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onItemClick(View view, int position) {
        File file = lists.get(position);
        if (file.isDirectory()) {
            File file1 = new File(curDirectory, file.getName() + "/");
            System.out.println(file1);
            initData(file1);
            adapter.notifyDataSetChanged();
        } else {
            if (fileSelection != null) {
                fileSelection.success(FileSelectionDialog.this, file);
            }
        }
    }


    public interface OnFileSelection {
        void success(Dialog dialog, File file);
    }

    @Override
    public void onClick(View v) {
    }
}
