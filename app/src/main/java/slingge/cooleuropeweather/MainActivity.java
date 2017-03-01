package slingge.cooleuropeweather;

import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import slingge.cooleuropeweather.adapter.RecyclerViewAdapter;
import slingge.cooleuropeweather.httpRequest.BiYingPicture;
import slingge.cooleuropeweather.util.AppJsonFileReader;
import slingge.cooleuropeweather.util.ToastUtil;
import slingge.cooleuropeweather.util.abLog;

import static slingge.cooleuropeweather.R.id.recyclerView;


/**
 * Created by Slingge on 2017/2/22 0022.
 */
public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.HolderCilck {

    private RecyclerViewAdapter adapter;
    private List<String> list = new ArrayList<>();

    private String province = "", city = "";
    private ImageView image_back;
    private TextView text;
    private BiYingPicture biYingPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initNavigationView();
        init();
    }

    private void init() {
        final ImageView image_bg = (ImageView) findViewById(R.id.image_bg);

        biYingPicture = new BiYingPicture(this);
        biYingPicture.getPicture();
        biYingPicture.setPictureCallBack(new BiYingPicture.PictureCallBack() {
            @Override
            public void Picture(String url) {
                abLog.e("..................",url);
                ImageLoader.getInstance().displayImage(url, image_bg);
            }
        });
    }

    private void initNavigationView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        text = (TextView) headerView.findViewById(R.id.text);
        image_back = (ImageView) headerView.findViewById(R.id.image_back);
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!city.equals("")) {
                    image_back.setVisibility(View.VISIBLE);
                    list.clear();
                    city = "";
                    text.setText(province);
                    list.addAll(AppJsonFileReader.getCityId(province, "", MainActivity.this));
                } else if (!province.equals("")) {
                    image_back.setVisibility(View.INVISIBLE);
                    list.clear();
                    text.setText("中国");
                    province = "";
                    list.addAll(AppJsonFileReader.getCityId("", "", MainActivity.this));
                }

                adapter.notifyDataSetChanged();
            }
        });
        RecyclerView recyclerView = (RecyclerView) headerView.findViewById(R.id.recyclerView);
        recyclerView.setFocusable(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list.clear();
        list.addAll(AppJsonFileReader.getCityId("", "", MainActivity.this));
        ToastUtil.showToast(list.size() + "");
        adapter = new RecyclerViewAdapter(this, list);
        adapter.setHolderCilck(this);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void click(int position) {
        String str = list.get(position);
        ToastUtil.showToast(str);
        if (province.equals("")) {
            province = str;
            text.setText(province);
            image_back.setVisibility(View.VISIBLE);
            list.clear();
            list.addAll(AppJsonFileReader.getCityId(province, "", MainActivity.this));
        } else if (city.equals("")) {
            city = str;
            text.setText(city);
            image_back.setVisibility(View.VISIBLE);
            list.clear();
            list.addAll(AppJsonFileReader.getCityId(province, city, MainActivity.this));
        }
        adapter.notifyDataSetChanged();
    }


}
