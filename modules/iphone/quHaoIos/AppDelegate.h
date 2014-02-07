//
//  AppDelegate.h
//  quHaoIos
//
//  Created by sam on 13-9-9.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "HomeViewController.h"
#import "NearViewController.h"
#import "MineViewController.h"
#import "SettingView.h"
#import "Helper.h"
#import "BaseNavigationController.h"
#import "ASIDownloadCache.h"

@interface AppDelegate : UIResponder <UIApplicationDelegate,UITabBarControllerDelegate>
{
    int m_lastTabIndex;
}
@property (strong, nonatomic) UIWindow *window;
@property (strong, nonatomic) UITabBarController *tabBarController;


@property (strong, nonatomic) HomeViewController * homeVc;
@property (strong, nonatomic) NearViewController *nearVc;
@property (strong, nonatomic) MineViewController *mineVc;
@property (strong, nonatomic) SettingView *moreVc;
@end
