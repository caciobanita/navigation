package com.navigation.reactnative;

import android.app.Activity;
import android.util.Pair;
import android.view.View;

import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class FragmentNavigator extends SceneNavigator {

    @Override
    void navigateBack(int currentCrumb, int crumb, Activity activity, NavigationStackView stack) {
        FragmentManager fragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();
        SceneFragment fragment = (SceneFragment) fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
        Pair[] sharedElements = getOldSharedElements(currentCrumb, crumb, fragment, stack);
        SceneFragment prevFragment = (SceneFragment) fragmentManager.findFragmentByTag(stack.getId() + "_" + stack.keys.getString(crumb));
        if (sharedElements != null && prevFragment != null && prevFragment.getScene() != null)
            prevFragment.getScene().transitioner = new SharedElementTransitioner(prevFragment, getSharedElementSet(stack.oldSharedElementNames));
        fragmentManager.popBackStack(stack.getId() + "_" + String.valueOf(crumb), 0);
    }

    @Override
    void navigate(int currentCrumb, int crumb, Activity activity, NavigationStackView stack) {
        final FragmentManager fragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();
        int enter = getAnimationResourceId(activity, stack.enterAnim, android.R.attr.activityOpenEnterAnimation);
        int exit = getAnimationResourceId(activity, stack.exitAnim, android.R.attr.activityOpenExitAnimation);
        for(int i = 0; i < crumb - currentCrumb; i++) {
            int nextCrumb = currentCrumb + i + 1;
            String key = stack.keys.getString(nextCrumb);
            SceneView scene = stack.scenes.get(key);
            int popEnter = getAnimationResourceId(activity, scene.enterAnim, android.R.attr.activityCloseExitAnimation);
            int popExit = getAnimationResourceId(activity, scene.exitAnim, android.R.attr.activityCloseEnterAnimation);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setReorderingAllowed(true);
            Pair[] sharedElements = null;
            if (nextCrumb > 0) {
                String prevKey = stack.keys.getString(nextCrumb - 1);
                SceneFragment prevFramgent = (SceneFragment) fragmentManager.findFragmentByTag(stack.getId() + "_" + prevKey);
                if (prevFramgent != null)
                    sharedElements = getSharedElements(currentCrumb, crumb, prevFramgent, stack);
            }
            if (sharedElements != null) {
                for(Pair sharedElement : sharedElements) {
                    fragmentTransaction.addSharedElement((View) sharedElement.first, (String) sharedElement.second);
                }
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            } else {
                fragmentTransaction.setCustomAnimations(oldCrumb != -1 ? enter : 0, exit, popEnter, popExit);
            }
            SceneFragment fragment = new SceneFragment(scene, getSharedElementSet(stack.sharedElementNames));
            fragmentTransaction.replace(stack.getChildAt(0).getId(), fragment, stack.getId() + "_" + key);
            fragmentTransaction.addToBackStack(stack.getId() + "_" + String.valueOf(nextCrumb));
            fragmentTransaction.commit();
        }
    }

    @Override
    void refresh(int currentCrumb, int crumb, Activity activity, NavigationStackView stack) {
        int enter = getAnimationResourceId(activity, stack.enterAnim, android.R.attr.activityOpenEnterAnimation);
        int exit = getAnimationResourceId(activity, stack.exitAnim, android.R.attr.activityOpenExitAnimation);
        String key = stack.keys.getString(crumb);
        SceneView scene = stack.scenes.get(key);
        int popEnter = getAnimationResourceId(activity, scene.enterAnim, android.R.attr.activityCloseExitAnimation);
        int popExit = getAnimationResourceId(activity, scene.exitAnim, android.R.attr.activityCloseEnterAnimation);
        FragmentManager fragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(enter, exit, popEnter, popExit);
        fragmentTransaction.replace(stack.getChildAt(0).getId(), new SceneFragment(scene, null), stack.getId() + "_" + key);
        fragmentTransaction.addToBackStack(stack.getId() + "_" + String.valueOf(crumb));
        fragmentTransaction.commit();
    }

    private Pair[] getOldSharedElements(int currentCrumb, int crumb, SharedElementContainer sharedElementContainer, final NavigationStackView stack) {
        final HashMap<String, View> oldSharedElementsMap = getSharedElementMap(sharedElementContainer.getScene());
        final Pair[] oldSharedElements = currentCrumb - crumb == 1 ? getSharedElements(oldSharedElementsMap, stack.oldSharedElementNames) : null;
        if (oldSharedElements != null && oldSharedElements.length != 0) {
            sharedElementContainer.setEnterCallback(new SharedElementCallback() {
                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> elements) {
                    for(int i = 0; i < stack.oldSharedElementNames.size(); i++) {
                        String name = stack.oldSharedElementNames.getString(i);
                        if (oldSharedElementsMap.containsKey(name)) {
                            View oldSharedElement = oldSharedElementsMap.get(name);
                            elements.put(names.get(i), oldSharedElement);
                        }
                    }
                }
            });
            return oldSharedElements;
        }
        return null;
    }

    private Pair[] getSharedElements(int currentCrumb, int crumb, SharedElementContainer sharedElementContainer, final NavigationStackView stack) {
        final HashMap<String, View> sharedElementsMap = getSharedElementMap(sharedElementContainer.getScene());
        final Pair[] sharedElements = crumb - currentCrumb == 1 ? getSharedElements(sharedElementsMap, stack.sharedElementNames) : null;
        if (sharedElements != null && sharedElements.length != 0) {
            sharedElementContainer.setExitCallback(new SharedElementCallback() {
                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> elements) {
                    for(int i = 0; i < names.size(); i++) {
                        String mappedName = names.get(i);
                        if (stack.oldSharedElementNames != null && stack.oldSharedElementNames.size() > i)
                            mappedName = stack.oldSharedElementNames.getString(i);
                        if (sharedElementsMap.containsKey(mappedName))
                            elements.put(names.get(i), sharedElementsMap.get(mappedName));
                    }
                }
            });
            return sharedElements;
        }
        return null;
    }
}
