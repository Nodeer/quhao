//
//  MerchartHistoryController.h
//  quHaoIos
//
//  Created by sam on 13-10-22.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MerchartModel.h"
#import "MJRefreshFooterView.h"
#import "MerchartDetail.h"
#import "HistoryCell.h"
#import "AppraiseViewController.h"
@interface MerchartHistoryController : UITableViewController
{
@private
    NSMutableArray *_reservationArray;
    UIBarButtonItem *_backButtonItem;
    UIBarButtonItem *_editItem ;
    UIBarButtonItem *_multiDeleteBarButton;
    UIBarButtonItem *_cancelBarButtonItem;
    UIButton * _mutiButton;
    NSMutableArray *_delArray;
}

@property (strong,nonatomic) NSString * accouId;
//加载页面上的导航
-(void)loadNavigationItem;
//弹出历史信息页面
- (void)pushHistoryDetail:(Reservation *)model andNavController:(UINavigationController *)navController;
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


