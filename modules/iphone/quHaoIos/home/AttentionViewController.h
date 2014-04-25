//
//  AttentionViewController.h
//  quHaoIos
//
//  Created by sam on 14-4-21.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MerchartModel.h"
#import "MerchartDetail.h"
#import "QuHaoUtil.h"
#import "MBProgressHUD.h"
@interface AttentionViewController : UITableViewController<MBProgressHUDDelegate>
{
@private
    NSMutableArray *_merchartsArray;
    MBProgressHUD *_HUD;
}
@property (strong,nonatomic) NSString * accountId;

//加载页面上的导航
-(void)loadNavigationItem;
//返回主页
- (void)clickToHome:(id)sender;
//弹出商家详细页面
- (void)pushMerchartDetail:(MerchartModel *)model andNavController:(UINavigationController *)navController andIsNextPage:(BOOL)isNextPage;
//请求服务端获取数据
-(void)requestData;
@end
