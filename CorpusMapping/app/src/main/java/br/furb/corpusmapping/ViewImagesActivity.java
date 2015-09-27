package br.furb.corpusmapping;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.furb.corpusmapping.data.ImageRecord;
import br.furb.corpusmapping.data.ImageRecordRepository;


public class ViewImagesActivity extends ActionBarActivity {
    Map<Long, List<ImageRecord>> moleImages;
    MoleImagesAdapter adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_images);
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView)findViewById(R.id.listImages);

        moleImages = ImageRecordRepository.getInstance(this).getLastMolesByPatientId(CorpusMappingApp.getInstance().getSelectedPatientId());

        List<ImageRecord> records = new ArrayList<ImageRecord>();

        for(List<ImageRecord> l : moleImages.values()){
            if(!l.isEmpty()){
                records.add(l.get(0));
            }
        }

        adapter = new MoleImagesAdapter(this, records);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
               /* ImageRecord moleImage = (ImageRecord)adapterView.getItemAtPosition(position);
                Toast.makeText(ViewImagesActivity.this,
                        moleImage.getMoleGroup().getGroupName(), Toast.LENGTH_SHORT).show();*/
                Intent intent = new Intent(ViewImagesActivity.this, MoleImageSliderActivity.class);
                List<ImageRecord> imageRecords = moleImages.get(id);
                intent.putExtra(MoleImageSliderActivity.PARAM_IMAGES, imageRecords.toArray(new ImageRecord[imageRecords.size()]));
                startActivity(intent);
            }
        });

        // EmptyView só funciona com ListActivity...
        listView.setEmptyView(findViewById(android.R.id.empty));
    }
}
