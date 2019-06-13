# Android侧滑菜单-SwipeMenuLayout

SwipeMenuLayout是一个零耦合的侧滑菜单，使用方式及其简单！只需要正常编写xml布局文件即可。

##目前功能如下
- 支持启用或禁用侧滑菜单
- 支持菜单在条目的左边或者右边
- 支持滑动阻塞或非阻塞
- 支持点击了menu后是否自动关闭menu
- 支持menu打开和关闭的回调监听
- 可快速打开和关闭menu

##简单用例
- 只需正常编写xml文件即可
- SwipeMenuLayout中第一个view为item布局，后面的为menu布局
- 关于布局的宽高问题，特殊情况简单说明一下
	 1. item的布局宽度始终会以match_parent测量
	 2. SwipeMenuLayout如果宽为warp_content的话，以父view的宽度为主，基本也是match_parent
	 3. SwipeMenuLayout高度为wrap_content的话，分两种情况，第一种是下面的view高度都是warp_content,那高度就是warp_content;如果其中的view高度有值的话，以数值最大的那个为SwipeMenuLayout的高度；如果SwipeMenuLayout的高度有准确值，例如60dp，下面的view高度即便超过60dp依旧也为60dp;

----------

	//第一步 项目根路径build中添加
    allprojects {
    	repositories {
    		...
    		maven { url 'https://jitpack.io' }
    	}
    }	
	//第二步 moudler中依赖
	dependencies {
		implementation 'com.github.ljphawk:SwipeMenuLayout:1.01'//tag为版本号
	}

	<!--父容器为SwipeMenuLayout后，正常编写xml就行啦 -->
	<!-- 下面示例中的值等于默认值 -->
    <cn.ljp.swipemenu.SwipeMenuLayout
	    xmlns:android="http://schemas.android.com/apk/res/android"
	    xmlns:app="http://schemas.android.com/apk/res-auto"
	    android:id="@+id/swipe_menu_layout"
	    android:layout_width="match_parent"
	    android:layout_height="60dp"
	    android:background="#e9e9e9"
	    app:isEnableLeftMenu="false"
	    app:isEnableSwipe="true"
	    app:isClickMenuAndClose="false"
	    app:isOpenChoke="true">
	    
		<!-- item布局为SwipeMenuLayout下的第一个view，后面的都是菜单 -->
	    <RelativeLayout
		    android:id="@+id/ll_item"
		    android:layout_width="match_parent"
		    android:layout_height="match_parent">
		    <!--也可以是复杂的item布局-->
			<TextView
			    android:id="@+id/tv_content"
			    android:layout_width="wrap_content"
			    android:layout_height="wrap_content"
			    android:gravity="center"/>
	    </RelativeLayout>
	    
	    <TextView
		    android:id="@+id/tv_menu1"
		    android:layout_width="60dp"
		    android:layout_height="60dp"
		    android:background="#fff"
	    	android:text="取消关注"/>
	    
	    <TextView
		    android:id="@+id/tv_menu2"
		    android:layout_width="60dp"
		    android:layout_height="60dp"
		    android:background="#f00"
		    android:text="删除"/>
    
    </cn.ljp.swipemenu.SwipeMenuLayout>


##属性说明
    
**代码示例**

	有set方法就有会对应的get方法，get方法我就不贴了
	set方法支持链式调用  

	SwipeMenuLayout swipeMenuLayout = findViewById(R.id.swipe_menu_layout);
    //是否启用侧滑菜单 默认是启用的
    swipeMenuLayout.setEnableSwipe(true);
    //设置菜单是否在item的左边，在左边的话是向右滑动，反之左滑（默认在item右边）
    swipeMenuLayout.setEnableLeftMenu(false);
    /*
       是否开启阻塞效果 默认开启。
       举个例子 比如你把item1的侧滑菜单划出来了，你继续滑动item2的，
       这是默认是开启阻塞效果的，在你滑动item2的时候 会先关闭item1的菜单，
       需要再次滑动item2才可以（qq是这样子的）
       如果关闭这个效果，你在滑动item2的同时会同时关闭item1
     */
    swipeMenuLayout.setOpenChoke(true);
    /*
    是否开启点击菜单后自动关闭菜单，默认false.
    思来想去决定还是把这个交给开发者决定应该在什么合适的时候来关闭
     */
    swipeMenuLayout.setClickMenuAndClose(false);
    //动画方式展开菜单 默认300ms
    swipeMenuLayout.expandMenuAnim();
    //动画方式关闭菜单 默认300ms
    swipeMenuLayout.closeMenuAnim();
    //快速打开菜单 0s
    swipeMenuLayout.quickExpandMenu();
    //快速关闭菜单 0s
    swipeMenuLayout.quickCloseMenu();
    //获取当前菜单是否展开
    swipeMenuLayout.isExpandMenu();
    //菜单打开关闭的监听。 true打开了 false关闭了
    swipeMenuLayout.setSwipeMenuStateListener(new SwipeMenuStateListener());

**xml代码设置**
    
	<!-- 下面示例中的值等于默认值 -->
    <cn.ljp.swipemenu.SwipeMenuLayout
	    xmlns:android="http://schemas.android.com/apk/res/android"
	    xmlns:app="http://schemas.android.com/apk/res-auto"
	    android:id="@+id/swipe_menu_layout"
	    android:layout_width="match_parent"
	    android:layout_height="60dp"
	    android:background="#e9e9e9"
	    app:isEnableLeftMenu="false"
	    app:isEnableSwipe="true"
	    app:isClickMenuAndClose="false"
	    app:isOpenChoke="true">
	    
	    <RelativeLayout
		    android:id="@+id/ll_item"
		    android:layout_width="match_parent"
		    android:layout_height="match_parent">
		    <!--也可以是复杂的item布局-->
			<TextView
			    android:id="@+id/tv_content"
			    android:layout_width="wrap_content"
			    android:layout_height="wrap_content"
			    android:gravity="center"/>
	    </RelativeLayout>
	    
	    <TextView
		    android:id="@+id/tv_menu1"
		    android:layout_width="60dp"
		    android:layout_height="60dp"
		    android:background="#fff"
	    	android:text="取消关注"/>
	    
	    <TextView
		    android:id="@+id/tv_menu2"
		    android:layout_width="60dp"
		    android:layout_height="60dp"
		    android:background="#f00"
		    android:text="删除"/>
    
    </cn.ljp.swipemenu.SwipeMenuLayout>

**属性表格 Attributes**

name | format | default | description
-|-|-|-
isEnableSwipe | boolean | true |是否启用侧滑
isEnableLeftMenu | boolean | false |菜单是否放置左边
isClickMenuAndClose | boolean | false |点击菜单后是否自动关闭
isOpenChoke | boolean | true |是否开启阻塞


**Method**

name | format | description
-|-|-
setEnableSwipe | SwipeMenuLayout | 是否启用侧滑 
setEnableLeftMenu | SwipeMenuLayout | 菜单是否放置左边 
setClickMenuAndClose | SwipeMenuLayout | 点击菜单后是否自动关闭 
setOpenChoke | SwipeMenuLayout | 是否开启阻塞 
expandMenuAnim |  | 动画方式展开菜单 
closeMenuAnim |  | 动画方式关闭菜单 
quickExpandMenu |  | 快速打开菜单 
quickCloseMenu |  | 快速关闭菜单 
isExpandMenu |  | 获取当前菜单是否展开 
setSwipeMenuStateListener | SwipeMenuStateListener | 菜单打开关闭的监听 