package com.quickblox.sample.chat.ui.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.quickblox.module.chat.QBChatService;
import com.quickblox.module.chat.listeners.RoomReceivingListener;
import com.quickblox.module.chat.model.QBChatRoom;
import com.quickblox.sample.chat.R;
import com.quickblox.sample.chat.core.RoomChat;
import com.quickblox.sample.chat.ui.activities.ChatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomsFragment extends Fragment implements RoomReceivingListener {

    private static final String KEY_ROOM_NAME = "roomName";
    private ListView roomsList;
    private List<QBChatRoom> rooms;
    private ProgressDialog progressDialog;

    public static RoomsFragment getInstance() {
        RoomsFragment roomsFragment = new RoomsFragment();
        return roomsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            progressDialog = ProgressDialog.show(getActivity(), null, "Loading rooms list");
        }
        QBChatService.getInstance().getRooms(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rooms, container, false);
        roomsList = (ListView) v.findViewById(R.id.roomsList);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.rooms, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Title");
            final EditText input = new EditText(getActivity());
            builder.setView(input);

            builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra(ChatActivity.EXTRA_MODE, ChatActivity.Mode.GROUP);
                    intent.putExtra(RoomChat.EXTRA_ROOM_NAME, input.getText().toString());
                    intent.putExtra(RoomChat.EXTRA_ROOM_ACTION, RoomChat.RoomAction.JOIN);

                    startActivity(intent);
                }
            });
            builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onReceiveRooms(List<QBChatRoom> list) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        rooms = list;

        // Prepare rooms list for simple adapter.
        final List<Map<String, String>> roomsListForAdapter = new ArrayList<Map<String, String>>();
        for (QBChatRoom room : rooms) {
            Map<String, String> roomMap = new HashMap<String, String>();
            roomMap.put(KEY_ROOM_NAME, room.getName());
            roomsListForAdapter.add(roomMap);
        }

        // Put rooms list into adapter.
        final SimpleAdapter roomsAdapter = new SimpleAdapter(getActivity(), roomsListForAdapter,
                R.layout.list_item_room,
                new String[]{KEY_ROOM_NAME},
                new int[]{R.id.roomName});

        roomsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(ChatActivity.EXTRA_MODE, ChatActivity.Mode.GROUP);
                intent.putExtra(RoomChat.EXTRA_ROOM_NAME, rooms.get(position).getName());
                intent.putExtra(RoomChat.EXTRA_ROOM_ACTION, RoomChat.RoomAction.JOIN);

                startActivity(intent);
            }
        });

        roomsList.setAdapter(roomsAdapter);
    }
}
