// Generated code from Butter Knife. Do not modify!
package com.infohold.trade;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ChangePasswordActivity$$ViewBinder<T extends com.infohold.trade.ChangePasswordActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624098, "field 'userNameTextView'");
    target.userNameTextView = finder.castView(view, 2131624098, "field 'userNameTextView'");
    view = finder.findRequiredView(source, 2131624099, "field 'oldPasswordEditText' and method 'onOldPasswordChanged'");
    target.oldPasswordEditText = finder.castView(view, 2131624099, "field 'oldPasswordEditText'");
    ((android.widget.TextView) view).addTextChangedListener(
      new android.text.TextWatcher() {
        @Override public void onTextChanged(
          java.lang.CharSequence p0,
          int p1,
          int p2,
          int p3
        ) {
          target.onOldPasswordChanged();
        }
        @Override public void beforeTextChanged(
          java.lang.CharSequence p0,
          int p1,
          int p2,
          int p3
        ) {
          
        }
        @Override public void afterTextChanged(
          android.text.Editable p0
        ) {
          
        }
      });
    view = finder.findRequiredView(source, 2131624100, "field 'newPasswordEditText' and method 'onNewPasswordChanged'");
    target.newPasswordEditText = finder.castView(view, 2131624100, "field 'newPasswordEditText'");
    ((android.widget.TextView) view).addTextChangedListener(
      new android.text.TextWatcher() {
        @Override public void onTextChanged(
          java.lang.CharSequence p0,
          int p1,
          int p2,
          int p3
        ) {
          target.onNewPasswordChanged();
        }
        @Override public void beforeTextChanged(
          java.lang.CharSequence p0,
          int p1,
          int p2,
          int p3
        ) {
          
        }
        @Override public void afterTextChanged(
          android.text.Editable p0
        ) {
          
        }
      });
    view = finder.findRequiredView(source, 2131624101, "field 'confirmEditText' and method 'onConfirmPasswordChanged'");
    target.confirmEditText = finder.castView(view, 2131624101, "field 'confirmEditText'");
    ((android.widget.TextView) view).addTextChangedListener(
      new android.text.TextWatcher() {
        @Override public void onTextChanged(
          java.lang.CharSequence p0,
          int p1,
          int p2,
          int p3
        ) {
          target.onConfirmPasswordChanged();
        }
        @Override public void beforeTextChanged(
          java.lang.CharSequence p0,
          int p1,
          int p2,
          int p3
        ) {
          
        }
        @Override public void afterTextChanged(
          android.text.Editable p0
        ) {
          
        }
      });
    view = finder.findRequiredView(source, 2131624102, "field 'saveButton' and method 'saveBtnAction'");
    target.saveButton = finder.castView(view, 2131624102, "field 'saveButton'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.saveBtnAction();
        }
      });
    view = finder.findRequiredView(source, 2131624191, "field 'toolbar'");
    target.toolbar = finder.castView(view, 2131624191, "field 'toolbar'");
  }

  @Override public void unbind(T target) {
    target.userNameTextView = null;
    target.oldPasswordEditText = null;
    target.newPasswordEditText = null;
    target.confirmEditText = null;
    target.saveButton = null;
    target.toolbar = null;
  }
}
