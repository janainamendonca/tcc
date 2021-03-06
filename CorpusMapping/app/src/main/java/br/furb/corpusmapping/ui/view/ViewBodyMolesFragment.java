package br.furb.corpusmapping.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import br.furb.corpusmapping.ui.common.BodyImageSliderActivity;
import br.furb.corpusmapping.R;
import br.furb.corpusmapping.data.model.SpecificBodyPart;
import br.furb.corpusmapping.util.ImageDrawer;

import static br.furb.corpusmapping.ui.common.BodyImageSliderActivity.PARAM_BODY_PARTS;
import static br.furb.corpusmapping.ui.common.BodyImageSliderActivity.PARAM_IMAGES;

/**
 * Fragment para visualização das pintas do tronco
 *
 * @author Janaina Carraro Mendonça Lima
 */
public class ViewBodyMolesFragment extends Fragment implements View.OnClickListener {

    private ImageView imgBack;
    private ImageView imgFront;

    public static ViewBodyMolesFragment newInstance() {
        ViewBodyMolesFragment fragment = new ViewBodyMolesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ViewBodyMolesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selection_body, container, false);

        imgFront = (ImageView) view.findViewById(R.id.imgBodyFront);
        imgBack = (ImageView) view.findViewById(R.id.imgBodyBack);

        imgFront.setOnClickListener(this);
        imgBack.setOnClickListener(this);

        // desenha as pintas associadas a esta região do corpo
        ImageDrawer.drawPointsOfBodyPart(imgFront, SpecificBodyPart.BODY_FRONT, R.drawable.tronco_frente);
        ImageDrawer.drawPointsOfBodyPart(imgBack, SpecificBodyPart.BODY_BACK, R.drawable.tronco_costas);
        return view;
    }


    @Override
    public void onClick(View v) {
        Intent i = new Intent(getActivity(), BodyImageSliderActivity.class);
        i.putExtra(BodyImageSliderActivity.PARAM_IS_TO_ASSOCIATE, false);
        i.putExtra(PARAM_IMAGES, new int[]{R.drawable.tronco_frente, R.drawable.tronco_costas});
        i.putExtra(PARAM_BODY_PARTS, new String[]{SpecificBodyPart.BODY_FRONT.name(), SpecificBodyPart.BODY_BACK.name()});
        int current = 0;

        switch (v.getId()) {
            case R.id.imgBodyFront:
                current = 0;
                break;
            case R.id.imgBodyBack:
                current = 1;
                break;
        }

        i.putExtra(BodyImageSliderActivity.PARAM_CURRENT, current);

        getActivity().startActivity(i);
    }

}
