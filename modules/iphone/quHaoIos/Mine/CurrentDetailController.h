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
#import "Reservation.h"
#import "LoginView.h"
#import "EGOImageView.h"
#import "HomeCell.h"
#import "QuHaoUtil.h"
#import "CommentViewController.h"
#import "YouHui.h"
@interface CurrentDetailController : UIViewController<UITableViewDataSource,UITableViewDelegate,UIAlertViewDelegate>
{
    UITableView *_detailView;
}

@property BOOL isNextPage;
@property (strong,nonatomic) NSString * merchartID;
@property (strong,nonatomic) NSString * accountID;
@property (strong,nonatomic) MerchartModel * single;
@property (strong,nonatomic) Reservation * reservation;
@property (strong,nonatomic) EGOImageView * egoImgView;
@property (strong,nonatomic) YouHui * youhui;
- (void)clickToHome:(id)sender;
//打开地图
- (void)pushMap:(NSString *)address andNavController:(UINavigationController *)navController andIsNextPage:(BOOL)isNextPage;
//拨打电话
-(void)CallPhone;
//展示用户点评
- (void)pushComment:(NSString *)cateType andNavController:(UINavigationController *)navController;
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex;
@end
