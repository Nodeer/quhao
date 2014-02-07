//
//  CurrentDetailController.h
//  quHaoIos
//
//  Created by sam on 13-11-17.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MerchartModel.h"
#import "MBProgressHUD.h"
#import "Helper.h"
#import "MerchartLocationController.h"
#import "Reservation.h"
#import "LoginView.h"
#import "EGOImageView.h"
#import "HomeCell.h"
#import "QuHaoUtil.h"
#import "CommentViewController.h"
@interface CurrentDetailController : UIViewController<UITableViewDataSource,UITableViewDelegate>
{
    UITableView *_detailView;
}

@property BOOL isNextPage;
@property (strong,nonatomic) NSString * merchartID;
@property (strong,nonatomic) NSString * accountID;
@property (strong,nonatomic) MerchartModel * single;
@property (strong,nonatomic) Reservation * reservation;
@property (strong,nonatomic) EGOImageView * egoImgView;

- (void)clickToHome:(id)sender;
//获取取号信息
-(void)reloadReversion;
//打开地图
- (void)pushMap:(NSString *)address andNavController:(UINavigationController *)navController andIsNextPage:(BOOL)isNextPage;
//初始化地图
- (void)initMapView;
//拨打电话
-(void)CallPhone;
//展示用户点评
- (void)pushComment:(NSString *)cateType andNavController:(UINavigationController *)navController;

@end
