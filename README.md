# VisibleAwareLayout
一个提供的可见性检测的android视图容器

## 说明
1、适用于各种需要view可见性监测的业务场景，譬如播放器自动启播和暂停，数据上报等等。 

2、多层嵌套的ViewPager和RecyclerView等滚动容器也适用。

## 引入
1、项目添加jitpack仓库地址:
```
maven { url 'https://jitpack.io' }
```

2、添加依赖: 
```
implementation 'com.github.iwtsus:VisibleAwareLayout:1.0.1'
```

## 使用
```
//设置屏幕中展示多少即视为可见，取值范围0-1，默认为1。 
visibleAwareLayout.setVisibleArea(1);


visibleAwareLayout.setVisibilityListener(new VisibleAwareLayout.VisibilityListener() {
    @Override
    public void onShow() {
        //不可见->可见
    }

    @Override
    public void onDismiss() {
        //可见->不可见
    }
});
```




