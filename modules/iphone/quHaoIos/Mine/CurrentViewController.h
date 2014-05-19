//
//  CurrentViewController.h
//  quHaoIos
//
//  Created by sam on 13-10-29.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MerchartModel.h"
#import "HomeCell.h"
#import "MerchartDetail.h"
#import "MBProgressHUD.h"
#import "DataSingleton.h"
typedef enum{
	EGOOPullRefreshPulling = 0,
	EGOOPullRefreshNormal,
	EGOOPullRefreshLoading,
} PullRefreshState;

@interface CurrentViewController : UIViewController<UITableViewDelegate,UITableViewDataSource,MBProgressHUDDelegate>
{
@private
    NSMutableArray *_merchartsArray;
    MBProgressHUD *_HUD;
    BOOL _isLoading;
    int _allCount;
    BOOL _isLoadOver;
    UIView *_tableFooterView;
    UILabel * _loadMoreText;
    UIActivityIndicatorView *_tableFooterActivityIndicator;
    PullRefreshState _state;
}

@property (strong,nonatomic) NSString * accouId;
@property (strong,nonatomic) UITableView * tableView;

//加载页面上的导航
-(void)loadNavigationItem;
-(void)clickToHome:(id)sender;
//请求服务端获取数据
-(void)requestData;
//弹出商家详细页面
-(void)pushCurrentDetail:(MerchartModel *)model andNavController:(UINavigationController *)navController;

@end
