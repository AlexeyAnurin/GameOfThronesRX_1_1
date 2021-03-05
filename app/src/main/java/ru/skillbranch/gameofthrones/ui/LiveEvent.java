package ru.skillbranch.gameofthrones.ui;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LiveEvent<T> extends MutableLiveData<T> {

    private List<ObserverWrapper<T>> observerWrappers = new ArrayList<>();

    @MainThread
    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        LiveEvent.ObserverWrapper<T> wrapper = new ObserverWrapper<>(observer);
        observerWrappers.add(wrapper);
        super.observe(owner, wrapper);
    }

    @MainThread
    @Override
    public void removeObserver(@NonNull Observer<? super T> observer) {
        if(observerWrappers.remove(observer)) {
            super.removeObserver(observer);
            return;
        }

        Iterator<ObserverWrapper<T>> iterator = observerWrappers.iterator();
        while(iterator.hasNext()) {
            ObserverWrapper<T> observerWrapper = iterator.next();
            if(observerWrapper.observer == observer) {
                iterator.remove();
                super.removeObserver(observerWrapper);
                break;
            }
        }
    }

    @MainThread
    @Override
    public void setValue(T value) {
        for(ObserverWrapper<T> observerWrapper : observerWrappers) {
            observerWrapper.newValue();
        }
        super.setValue(value);
    }

    private static class ObserverWrapper<T> implements Observer<T> {

        private boolean pending = false;
        private Observer<? super T> observer;

        ObserverWrapper(Observer<? super T> observer) {
            this.observer = observer;
        }

        @Override
        public void onChanged(T o) {
            if (pending) {
                pending = false;
                observer.onChanged(o);
            }
        }

        void newValue() {
            pending = true;
        }

    }
}