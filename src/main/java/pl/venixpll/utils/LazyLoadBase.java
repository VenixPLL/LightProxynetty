package pl.venixpll.utils;

public abstract class LazyLoadBase<T>
{
    private T value;
    private boolean loaded;

    public T getValue() {
        if (!loaded) {
            loaded = true;
            value = this.load();
        }

        return value;
    }

    protected abstract T load();
}
