//
//  CreditViewController.h
//  quHaoIos
//
//  Created by sam on 13-11-24.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MerchartModel.h"
#import "ASIHTTPRequest.h"
#import "SBJson.h"
#import "CreditCell.h"
#import "Helper.h"
#import "QuHaoUtil.h"
@interface CreditViewController : UITableViewController<MBProgressHUDDelegate>
{
@private
    NSMutableArray *_creditArray;
    UIBarButtonItem *_backButtonItem;
    UIBarButtonItem *_editItem ;
    UIBarButtonItem *_multiDeleteBarButton;
    UIBarButtonItem *_cancelBarButtonItem;
    UIButton * _mutiButton;
    NSMutableArray *_delArray;
    MBProgressHUD *_HUD;
}

@property (retain,nonatomic) NSString * accouId;

//加载页面上的导航
-(void)loadNavigationItem;
//请求服务端获取数据
-(void)requestData:(NSString *)urlStr;
// 更新导航栏按钮
-(void) updateBarButtons;
// 更新删除按钮的标题
-(void)updateDeleteButtonTitle;
//删除按钮
- (void)multiDeleteClicked:(id)sender;
// 取消按钮
- (void)cancelButtonClicked:(id)sender;
@end