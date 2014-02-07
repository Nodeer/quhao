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
#import "CurrentDetailController.h"
#import "MJRefreshFooterView.h"
#import "MerchartDetail.h"
@interface CurrentViewController : UITableViewController
{
@private
    NSMutableArray *_merchartsArray;
    BOOL _reloading;
    BOOL loadFlag;
    int prevItemCount;
    //上拉刷新用的页码
    int pageIndex;
    //上拉刷新的view
    MJRefreshFooterView *_footer;
}

@property (strong,nonatomic) NSString * accouId;

//加载页面上的导航
-(void)loadNavigationItem;
-(void)clickToHome:(id)sender;
//请求服务端获取数据
-(void)requestData:(NSString *)urlStr withPage:(int)page;
//弹出商家详细页面
-(void)pushCurrentDetail:(MerchartModel *)model andNavController:(UINavigationController *)navController;

@end
