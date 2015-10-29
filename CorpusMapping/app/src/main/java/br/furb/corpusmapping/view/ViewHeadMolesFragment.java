package br.furb.corpusmapping.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import br.furb.corpusmapping.capture.BodyImageSliderActivity;
import br.furb.corpusmapping.R;
import br.furb.corpusmapping.SpecificBodyPart;
import br.furb.corpusmapping.util.ImageDrawer;

import static br.furb.corpusmapping.capture.BodyImageSliderActivity.PARAM_BODY_PARTS;
import static br.furb.corpusmapping.capture.BodyImageSliderActivity.PARAM_IMAGES;
import static br.furb.corpusmapping.SpecificBodyPart.HEAD_BACK;
import static br.furb.corpusmapping.SpecificBodyPart.HEAD_FRONT;
import static br.furb.corpusmapping.SpecificBodyPart.HEAD_LEFT;
import static br.furb.corpusmapping.SpecificBodyPart.HEAD_RIGHT;

/**
 * Fragment para visualização das pintas da cabeça e pescoço
 */
public class ViewHeadMolesFragment extends Fragment implements View.OnClickListener {

    private ImageView imgHeadFront;
    private ImageView imgHeadBack;
    private ImageView imgHeadLeft;
    private ImageView imgHeadRight;

    public static ViewHeadMolesFragment newInstance() {
        ViewHeadMolesFragment fragment = new ViewHeadMolesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ViewHeadMolesFragment() {
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
        View view = inflater.inflate(R.layout.fragment_selection_head, container, false);

        imgHeadFront = (ImageView) view.findViewById(R.id.imgHeadFront);
        imgHeadBack = (ImageView) view.findViewById(R.id.imgHeadBack);
        imgHeadLeft = (ImageView) view.findViewById(R.id.imgHeadLeft);
        imgHeadRight = (ImageView) view.findViewById(R.id.imgHeadRight);

        imgHeadFront.setOnClickListener(this);
        imgHeadBack.setOnClickListener(this);
        imgHeadLeft.setOnClickListener(this);
        imgHeadRight.setOnClickListener(this);

        // desenha as pintas associadas a cabeça
        ImageDrawer.drawPoints(imgHeadFront, HEAD_FRONT, R.drawable.cabeca_frente);
        ImageDrawer.drawPoints(imgHeadBack, HEAD_BACK, R.drawable.cabeca_atras);
        ImageDrawer.drawPoints(imgHeadLeft, HEAD_LEFT, R.drawable.cabeca_esquerda);
        ImageDrawer.drawPoints(imgHeadRight, HEAD_RIGHT, R.drawable.cabeca_direita);

        return view;
    }


    @Override
    public void onClick(View v) {
        Intent i = new Intent(getActivity(), BodyImageSliderActivity.class);
        i.putExtra(BodyImageSliderActivity.PARAM_IS_TO_ASSOCIATE, false);
        i.putExtra(PARAM_IMAGES, new int[]{R.drawable.cabeca_frente, R.drawable.cabeca_atras, R.drawable.cabeca_direita, R.drawable.cabeca_esquerda});
        i.putExtra(PARAM_BODY_PARTS, new String[]{SpecificBodyPart.HEAD_FRONT.name(), SpecificBodyPart.HEAD_BACK.name(), SpecificBodyPart.HEAD_RIGHT.name(), SpecificBodyPart.HEAD_LEFT.name()});
        int current = 0;
        switch (v.getId()) {
            case R.id.imgHeadFront:
                current  = 0;
                break;
            case R.id.imgHeadBack:
                current  = 1;
                break;
            case R.id.imgHeadLeft:
                current  = 2;
                break;
            case R.id.imgHeadRight:
                current  = 3;
                break;
        }

        i.putExtra(BodyImageSliderActivity.PARAM_CURRENT, current);
        getActivity().startActivity(i);
    }

}
