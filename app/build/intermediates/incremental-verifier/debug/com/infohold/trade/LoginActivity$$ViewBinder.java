// Generated code from Butter Knife. Do not modify!
package com.infohold.trade;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class LoginActivity$$ViewBinder<T extends com.infohold.trade.LoginActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624104, "field 'userNameEditText'");
    target.userNameEditText = finder.castView(view, 2131624104, "field 'userNameEditText'");
    view = finder.findRequiredView(source, 2131624105, "field 'passwordTextEdit'");
    target.passwordTextEdit = finder.castView(view, 2131624105, "field 'passwordTextEdit'");
    view = finder.findRequiredView(source, 2131624106, "field 'loginButton' and method 'login'");
    target.loginButton = finder.castView(view, 2131624106, "field 'loginButton'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.login();
        }
      });
  }

  @Override public void unbind(T target) {
    target.userNameEditText = null;
    target.passwordTextEdit = null;
    target.loginButton = null;
  }
}
