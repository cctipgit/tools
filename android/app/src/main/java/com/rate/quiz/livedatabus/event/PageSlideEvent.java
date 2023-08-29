package com.rate.quiz.livedatabus.event;

/**
 * 页面滑动事件，开始滑动和结束滑动都会发送
 */
public class PageSlideEvent {

    /**
     * 事件类型：0开始滑动、1取消滑动、2滑动完毕ß
     */
    public int type;

    /**
     * 触发事件的对象
     */
    public Class object;

    /**
     * @param type   事件类型：0开始滑动、1取消滑动、2滑动完毕
     * @param object 触发事件的对象
     */
    public PageSlideEvent(int type, Class object) {
        this.type = type;
        this.object = object;
    }
}
