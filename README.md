###### ExpandCollpaseTextView是可以展开和收起的文本控件
###### 添加依赖：
* 在项目根目录下的build.gradle添加如下依赖：
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
* 在要使用的模块目录下的build.gradle添加如下依赖：
```
dependencies {
	        implementation 'com.github.jxiang112:ExpandCollapseTextView:v1.0.2'
	}
```
###### 特性：
* 可以设置收起状态下显示的行数
* 可以设置收起状态下，最后一行剩余空白的占比
* 可以设置展开/收起按钮：显示的文字、字体颜色、字体大小
* 可以设置点击展开和收起的事件源：全部文字的点击事件都可以展开/收起、只有点击展开/收起按钮才可以展开收起

###### 原理：
* 使用TextView的maxLine，收起的时候设置maxLine为多少行，展开时设置maxLine为Int的最大值
* 使用Paint的measureText来计算文本的宽度、行数；如果测量的文本宽度、行数比设置的收起状态下的行数小，则就不需要展开/收起功能；如果超过了收起状态下的行数，则会有展开/收起的功能，并计算收起状态下最后一行能显示的文字

###### 属性：
属性名称                      |        属性类型      |       属性默认值      |       属性描述
---|:---|:---|:---:
expand_text                  |         string       |        展开>>        |       展开按钮显示的文本
collapse_text                |         string       |        收起>>        |       收起按钮显示的文本
expand_text_size             |         integer      |        14            |       展开/折叠文本字体大小，单位sp
expand_text_color            |         color        |        #00C25F       |       展开/折叠文本颜色
content_text                 |         string       |                      |       文本内容
text_line_height             |         dimension    |       0              |       行高
content_text_size            |         integer      |       14             |       文本文字大小
content_text_color           |         color        |       #333333        |       文本文字颜色
collapse_show_line_number    |         integer      |       2              |       折叠状态下，显示的行数
expand_state                 |         enum         |       collapse       |      设置展开折叠状态，collapse: 折叠状态；expend：展开状态
collapse_line_space_percent  |         integer      |       20             |  折叠状态下，最后一行空白所占宽度的百分比，0-100
expend_click_event_on        |         enum         |       all            |  点击可以展开/收起的事件源，all:点击文本的任何地方都可以展开/收起；expand_text：只有点击展开/收起按钮才可以展开/收起
###### 使用示例：
```
//xml布局文件中：
<com.wyx.components.widgets.ExpandCollpaseTextView
        android:id="@+id/expand_textview"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginTop="12dp"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:content_text_color="#999999"
        app:content_text_size="12"
        app:text_line_height="2dp"
        app:expand_text_color="#06C362"
        app:collapse_line_space_percent="70"
        />
//java文件中：
ExpandCollpaseTextView expandTextView = findViewById(R.id.expand_textview);
expandTextView.setText("XXXXXXXXXXXXXXXXXXX");

```	
###### 效果预览：
![](http://m.qpic.cn/psb?/V11udxUS3o4KXH/p051QTqcVWbGHwyid4MbkcnFqx6ZJHs7HNsOkz*HQow!/b/dLkAAAAAAAAA&bo=WALfBAAAAAACR.I!&rf=viewer_4)
