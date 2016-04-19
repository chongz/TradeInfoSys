// Generated code from Butter Knife. Do not modify!
package com.infohold.trade;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class SplashActivity$$ViewBinder<T extends com.infohold.trade.SplashActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624121, "field 'imageView'");
    target.imageView = finder.castView(view, 2131624121, "field 'imageView'");
    view = finder.findRequiredView(source, 2131624123, "field 'versionTextView'");
    target.versionTextView = finder.castView(view, 2131624123, "field 'versionTextView'");
  }

  @Override public void unbind(T target) {
    target.imageView = null;
    target.versionTextView = null;
  }
}
