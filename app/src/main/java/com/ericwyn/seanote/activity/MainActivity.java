package com.ericwyn.seanote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ericwyn.seanote.R;
import com.ericwyn.seanote.adapter.MainRvAdapter;
import com.ericwyn.seanote.entity.Note;
import com.ericwyn.seanote.server.SeafileServer;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG="MainActivity";
    private static boolean repoIdCheckFlag=false;
    private RecyclerView notesRecyclerView;
    private MainRvAdapter mainRvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.main_act_toolbar_title);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mainRvAdapter.addHeaderView()
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        notesRecyclerView=(RecyclerView)findViewById(R.id.noteRecyclerView_MainAct);
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        mainRvAdapter=new MainRvAdapter();
        mainRvAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                Intent intent=new Intent(MainActivity.this,PreviewActivity.class);
                Bundle bundle=new Bundle();
                Note note=(Note)baseQuickAdapter.getItem(i);
                bundle.putString("filePath",note.getFilePath());
                bundle.putString("title",note.getTitle());
                bundle.putString("word",note.getWords());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        mainRvAdapter.openLoadAnimation();
        notesRecyclerView.setAdapter(mainRvAdapter);
//        notesRecyclerView.setVisibility(View.INVISIBLE);
        //successed in creating the seanote's library and getting repo_id for the further development
        //验证repo_id 是否是错误的或者不存在的
        checkoutRepoID();



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void checkoutRepoID(){
        if(!repoIdCheckFlag){
            if(!SeafileServer.getRepo_id().equals("null")){
                SeafileServer.getApi().getLibraryInfo(SeafileServer.getClient(), SeafileServer.getToken(), SeafileServer.getRepo_id(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Looper.prepare();
                        Toast.makeText(MainActivity.this,R.string.network_error,Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(response.isSuccessful()){
                            Log.d(TAG,"repo_id验证成功");
//                        Toast.makeText(MainActivity.this,R.string.)
                        }else {
                            Looper.prepare();
                            Toast.makeText(MainActivity.this,R.string.main_act_lib_expired,Toast.LENGTH_SHORT).show();
                            SeafileServer.createSeanoteLib(MainActivity.this);
                            Looper.loop();
                        }
                    }
                });
            }else {
                SeafileServer.createSeanoteLib(MainActivity.this);
            }
            repoIdCheckFlag=true;
        }
    }

}
