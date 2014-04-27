//
//  MineViewController.h
//  quHaoApp
//
//  Created by sam on 13-7-28.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Helper.h"
#import "LoginView.h"
#import "UserInfo.h"
#import "MerchartHistoryController.h"
#import "CurrentViewController.h"
#import "CreditViewController.h"
#import "CommentViewController.h"
#import "EGOImageView.h"
#import "MineInfoViewController.h"
#import "AttentionViewController.h"
@interface MineViewController : UIViewController<UITableViewDataSource,UITableViewDelegate,UIActionSheetDelegate,UIImagePickerControllerDelegate,UINavigationControllerDelegate>
{
    UITableView *_mineView;
    Helper *_helper;
    UserInfo * _userInfo;
}

@property BOOL isLoginJustNow;
@property (strong,nonatomic) EGOImageView * egoImgView;
//点击签到的
-(void)onClickUILable:(UITapGestureRecognizer *)sender;
//点击点评
-(void)onClickDp:(UITapGestureRecognizer *)sender;
//点击历史取号
- (void)pushHistoryMerchart;
//点击当前取号
- (void)pushCurrentMerchart;
//点击消费情况
- (void)pushCreditView;
//查询用户信息
-(void)reload;
//登录事件
- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex;
//登录监听
- (void)noticeUpdateHandler:(NSNotification *)notification;

@end
