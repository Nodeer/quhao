//
//  HomeViewController.h
//  quHaoApp
//
//  Created by sam on 13-7-28.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MerchartModel.h"
#import "MerchartDetail.h"
#import "QuHaoUtil.h"
#import "SearchView.h"
#import "MBProgressHUD.h"

@interface ListViewController : UITableViewController<MBProgressHUDDelegate>
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

@property (strong,nonatomic) NSString * currentDateStr;
@property (strong,nonatomic) NSString * cateType;
@property (strong,nonatomic) NSString * cityCode;

//加载页面上的导航
-(void)loadNavigationItem;
//返回主页
- (void)clickToHome:(id)sender;
//搜索的点击事件
- (void)clickSearch:(id)sender;
//弹出商家详细页面
- (void)pushMerchartDetail:(MerchartModel *)model andNavController:(UINavigationController *)navController andIsNextPage:(BOOL)isNextPage;
//请求服务端获取数据
-(void)requestData;

@end
