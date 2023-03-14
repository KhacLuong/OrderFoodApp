package com.example.orderfood.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.orderfood.ControllerApplication;
import com.example.orderfood.R;
import com.example.orderfood.activity.MainActivity;
import com.example.orderfood.constant.GlobalFunction;
import com.example.orderfood.databinding.FragmentFeedbackBinding;
import com.example.orderfood.model.Feedback;
import com.example.orderfood.utils.StringUtils;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class FeedbackFragment extends BaseFragment{

    private FragmentFeedbackBinding mFragmentFeedbackBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentFeedbackBinding = FragmentFeedbackBinding.inflate(inflater, container, false);
        mFragmentFeedbackBinding.tvSendFeedback.setOnClickListener(view -> onClickSendFeedback());
        return mFragmentFeedbackBinding.getRoot();
    }
    private void onClickSendFeedback() {
        if (getActivity() == null) {
            return;
        }

        MainActivity activity = (MainActivity) getActivity();
        String strName = mFragmentFeedbackBinding.edtName.getText().toString();
        String strPhone = mFragmentFeedbackBinding.edtPhone.getText().toString();
        String strEmail = mFragmentFeedbackBinding.edtEmail.getText().toString();
        String strComment = mFragmentFeedbackBinding.edtComment.getText().toString();

        if (StringUtils.isEmpty(strName)) {
            GlobalFunction.showToastMessage(activity, getString(R.string.name_require));
        } else if (StringUtils.isEmpty(strComment)) {
            GlobalFunction.showToastMessage(activity, getString(R.string.comment_require));
        } else{
            activity.showProgressDialog(true);
            Feedback feedback = new Feedback(strName, strPhone, strEmail, strComment );
            ControllerApplication.get(getActivity()).getFeedbackDatabaseReference()
                    .child(String.valueOf(System.currentTimeMillis()))
                    .setValue(feedback, (error, ref) -> {
                        activity.showProgressDialog(false);
                        sendFeedbackSuccess();
                    }
                    );
        }
    }

    private void sendFeedbackSuccess() {
        GlobalFunction.hideSoftKeyboard(getActivity());
        GlobalFunction.showToastMessage(getActivity(), getString(R.string.send_feedback_success));
        mFragmentFeedbackBinding.edtName.setText("");
        mFragmentFeedbackBinding.edtPhone.setText("");
        mFragmentFeedbackBinding.edtEmail.setText("");
        mFragmentFeedbackBinding.edtComment.setText("");
    }

    @Override
    protected void initToolbar() {
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolBar(false, getString(R.string.feedback));
        }
    }
}
