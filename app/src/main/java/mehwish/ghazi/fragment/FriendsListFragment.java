package mehwish.ghazi.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;

import mehwish.ghazi.R;
import mehwish.ghazi.adapter.FriendsListAdapter;
import mehwish.ghazi.model.FriendsListAndRequestModel;

/**
 * Created by Devprovider on 3/7/2017.
 */

public class FriendsListFragment extends Fragment implements View.OnClickListener {

    private static final String FRAGMENT_TAG = "friendslistfragment";
    private List<FriendsListAndRequestModel> dataList = new ArrayList<>();
    private ListView friendsListLV;
    private Context context;
    private AlertDialog unfriendDialog;
    private static int toDelete;
    private Animation animRightSwipe;
    private Animation animLeftSwipe;

    public FriendsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //showInstructions();
        return inflater.inflate(R.layout.fragment_friends_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        friendsListLV = (ListView) view.findViewById(R.id.friends_list_LV);
        getTempData();
        setData();
    }

    public void showInstructions() {
        final AlertDialog swipeLeftDialog = new AlertDialog.Builder(getActivity(), R.style.swipe_left_instruction_dialog).
                create();
        swipeLeftDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeLeftDialog.dismiss();
                final AlertDialog swipeRightDialog = new AlertDialog.Builder(getActivity(), R.style.swipe_right_instruction_dialog)
                        .create();
                swipeRightDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRightDialog.dismiss();
                    }
                }, 3000);
            }
        }, 3000);
    }

    public void setData() {
        animRightSwipe = AnimationUtils.loadAnimation(getActivity(), R.anim.trans_right_out);
        animRightSwipe.setDuration(500);
        animLeftSwipe = AnimationUtils.loadAnimation(getActivity(), R.anim.trans_left_out);
        FriendsListAdapter adapter = new FriendsListAdapter(context, dataList);
        friendsListLV.setAdapter(adapter);
        friendsListLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = dataList.get(position).getFriendContactNo();
                FriendsDetailsFragment fragment = FriendsDetailsFragment.newInstance(name);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.container_body, fragment).
                        addToBackStack(FRAGMENT_TAG).commit();
            }
        });
        friendsListLV.setOnTouchListener(new SwipeListener(getActivity(), friendsListLV));

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void showDeleteFriendDialog() {
        unfriendDialog = new AlertDialog.Builder(context).setTitle("Unfriend " + dataList.get(toDelete).getFriendName() + "?").
                setView(R.layout.unfriend_dialog_layout).create();
        unfriendDialog.setCancelable(true);
        unfriendDialog.setCanceledOnTouchOutside(true);
        unfriendDialog.show();
        TextView name = (TextView) unfriendDialog.findViewById(R.id.unfriend_name_tv);
        Button cancel = (Button) unfriendDialog.findViewById(R.id.cancel_unfriend);
        Button unfriend = (Button) unfriendDialog.findViewById(R.id.do_unfriend);
        name.setText(dataList.get(toDelete).getFriendName());
        cancel.setOnClickListener(this);
        unfriend.setOnClickListener(this);
    }

    public void getTempData() {
        for (int i = 0; i < 12; i++) {
            dataList.add(new FriendsListAndRequestModel(0, "Mehwish Ghazi" + i, "+92312345678" + i));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_unfriend:
                unfriendDialog.dismiss();
                break;
            case R.id.do_unfriend:
                dataList.remove(toDelete);
                setData();
                unfriendDialog.dismiss();
        }
    }

    public class SwipeListener implements View.OnTouchListener {

        private ListView list;
        private Context context;
        private GestureDetector gestureDetector;

        public SwipeListener(Context ctx, ListView list) {
            gestureDetector = new GestureDetector(ctx, new GestureListener());
            context = ctx;
            this.list = list;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        public void onSwipeRight(int pos) {
            View view = friendsListLV.getChildAt(pos);
            if (view != null)
                view.startAnimation(animRightSwipe);
            Toast.makeText(context, "Message Friend (TODO)!", Toast.LENGTH_LONG).show();
        }

        public void onSwipeLeft(int pos) {
            View view = friendsListLV.getChildAt(pos);
            if (view != null)
                view.startAnimation(animLeftSwipe);
            toDelete = pos;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showDeleteFriendDialog();
                }
            }, 300);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 3;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            private int getPostion(MotionEvent e1) {
                return list.pointToPosition((int) e1.getX(), (int) e1.getY());
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2,
                                   float velocityX, float velocityY) {
                float distanceX = e2.getX() - e1.getX();
                float distanceY = e2.getY() - e1.getY();
                if (Math.abs(distanceX) > Math.abs(distanceY)
                        && Math.abs(distanceX) > SWIPE_THRESHOLD
                        && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (distanceX > 0)
                        onSwipeRight(getPostion(e1));
                    else
                        onSwipeLeft(getPostion(e1));
                    return true;
                }
                return false;
            }

        }
    }

}
