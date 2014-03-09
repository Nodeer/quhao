//
//  SettingView.h
//  quHaoApp
//
//  Created by sam on 13-9-30.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SettingModel.h"
#import "SearchView.h"
#import "Helper.h"
#import "ASIHTTPRequest.h"
#import "SBJson.h"
#import "About.h"
#import "FeedbackViewController.h"
#import "EGOCache.h"
#import "OAuthWebViewController.h"
@interface SettingView : UIViewController<UITableViewDataSource, UITableViewDelegate,UIAlertViewDelegate>
{
    NSArray * _settings;
    NSMutableDictionary * _settingsInSection;
    BOOL _showImage;//是否显示图片
    NSString *_plistPath;//文件存储位置
}

@property (strong, nonatomic) IBOutlet UITableView *tableSettings;
@property (strong,nonatomic) NSArray * settings;
@property (strong,nonatomic) NSMutableDictionary * settingsInSection;

- (void)refresh;
//检查更新
- (void)checkVersionNeedUpdate;
//比较版本
+ (int)getVersionNumber:(NSString *)version;
//下载
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex;
//清除缓存
-(void)dirCache;
@end
