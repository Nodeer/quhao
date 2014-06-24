package com.withiter.common.jpush;

import play.Logger;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;

public class JPushReminder {

	static String ALERT = "Push 测试";
	static String MSG_CONTENT = "push 测试";
	static String masterSecret = "cc89a6e23c2d3bdc0169467e";
	static String appKey = "3f817561ac185454655e6582";
	
	
	
	/**
	 * 快捷地构建推送对象：所有平台，所有设备，内容为 ALERT 的通知
	 * 
	 * @return
	 */
	public static PushPayload buildPushObject_all_all_alert() {
		return PushPayload.alertAll(ALERT);
	}

	/**
	 * 构建推送对象：所有平台，推送目标是别名为 "alias1"，通知内容为 ALERT
	 * 
	 * @return
	 */
	public static PushPayload buildPushObject_all_alias_alert() {
		return PushPayload.newBuilder().setPlatform(Platform.all()).setAudience(Audience.alias("alias1")).setNotification(Notification.alert(ALERT)).build();
	}

	/**
	 * 构建推送对象：平台是 Android，目标是 tag 为 "tag1" 的设备，内容是 Android 通知 ALERT
	 * 
	 * @return
	 */
	public static PushPayload buildPushObject_android_tag_alertWithTitle() {
		return PushPayload.newBuilder().setPlatform(Platform.android()).setAudience(Audience.tag("tag1"))
				.setNotification(Notification.newBuilder().addPlatformNotification(AndroidNotification.newBuilder().setAlert(ALERT).build()).build()).build();
	}

	/**
	 * 构建推送对象：平台是 iOS，推送目标是 "tag1", "tag_all" 的并集，推送内容同时包括通知与消息 - 通知信息是
	 * ALERT，并且附加字段 from = "JPush"；消息内容是 MSG_CONTENT。通知是 APNs 推送通道的，消息是 JPush
	 * 应用内消息通道的。
	 * 
	 * @return
	 */
	public static PushPayload buildPushObject_ios_tagAnd_alertWithExtrasAndMessage() {
		return PushPayload.newBuilder().setPlatform(Platform.ios()).setAudience(Audience.tag_and("tag1", "tag_all"))
				.setNotification(Notification.newBuilder().addPlatformNotification(IosNotification.newBuilder().setAlert(ALERT).addExtra("from", "JPush").build()).build())
				.setMessage(Message.content(MSG_CONTENT)).build();
	}

	/**
	 * 构建推送对象：平台是 Andorid 与 iOS，推送目标是 （"tag1" 与 "tag2" 的交集）并（"alias1" 与 "alias2"
	 * 的交集），推送内容是 - 内容为 MSG_CONTENT 的消息，并且附加字段 from = JPush。
	 * 
	 * @return
	 */
	public static PushPayload buildPushObject_ios_audienceMore_messageWithExtras() {
		return PushPayload.newBuilder().setPlatform(Platform.android_ios())
				.setAudience(Audience.newBuilder().addAudienceTarget(AudienceTarget.tag("tag1", "tag2")).addAudienceTarget(AudienceTarget.alias("alias1", "alias2")).build())
				.setMessage(Message.newBuilder().setMsgContent(MSG_CONTENT).addExtra("from", "JPush").build()).build();
	}

	public static void sendAlias(String id, String content) {
		PushPayload payload = PushPayload.newBuilder().setPlatform(Platform.all()).setAudience(Audience.alias(id)).setNotification(Notification.alert(content)).build();
		
		// TODO set to true when using under production modal
		payload.resetOptionsApnsProduction(false);
		Logger.debug("Paylaod JSON - " + payload.toString());
		JPushClient jpushClient = new JPushClient(masterSecret, appKey);
		PushResult result = jpushClient.sendPush(payload);
		if (result.isResultOK()) {
			Logger.debug(result.toString());
		} else {
			if (result.getErrorCode() > 0) {
				Logger.error(result.getOriginalContent());
			} else {
				Logger.error("Maybe connect error. Retry laster. ");
			}
		}
		
	}
}
