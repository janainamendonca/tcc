package br.furb.corpusmapping.ui.data;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import br.furb.corpusmapping.R;

public class DataFragment extends Fragment implements View.OnClickListener {

    public static DataFragment newInstance() {
        return new DataFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_data, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Button btnBackup = (Button) view.findViewById(R.id.btnBackup);
        final Button btnRestore = (Button) view.findViewById(R.id.btnRestore);
        final Button btnRestoreAndMerge = (Button) view.findViewById(R.id.btnRestoreAndMerge);

        btnBackup.setOnClickListener(this);
        btnRestore.setOnClickListener(this);
        btnRestoreAndMerge.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBackup:
                ExportActivity.start(getActivity());
                break;
            case R.id.btnRestore:
                ImportActivity.start(getActivity(), ImportActivity.ImportType.Backup);
                break;
            case R.id.btnRestoreAndMerge:
                ImportActivity.start(getActivity(), ImportActivity.ImportType.MergeBackup);
                break;
        }
    }
}
