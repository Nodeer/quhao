//
//  HomeViewController.h
//  quHaoIos
//
//  Created by sam on 13-10-5.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <QuartzCore/QuartzCore.h>
#import "ASIHTTPRequest.h"
#import "Helper.h"
#import "ListViewController.h"
#import "SBJson.h"
#import "Category.h"
#import "UICustomLabel.h"
#import "SearchView.h"
#import "CityViewController.h"
#import "EScrollerView.h"
#import "AttentionViewController.h"
@interface HomeViewController : UIViewController<CityViewDelegate,EScrollerViewDelegate>
{
    NSMutableArray *_categoryArray;
    NSMutableArray *_topUrlArray;
    NSMutableArray *_topIdArray;
    NSArray *_middleBtn;
    int _columns;
    int _columnInc;
    CGFloat _marginSize;
    CGFloat _gutterSize;
    CGFloat _rowHeight;
    CGFloat _xOffset;
    CGFloat _yOffset;
    UIInterfaceOrientation _rotation;
    UIButton *_cityButton;
    NSString *_cityCode;
    BOOL _isLoading;
}

@property (nonatomic, strong) UIScrollView *menuView;
@property (nonatomic, assign) BOOL wrap;
@property (nonatomic, strong) NSMutableArray *items;

//@property (nonatomic, strong) iCarousel *carousel;
-(void)requestTopData;
-(void)requestMenuData;
//创建catogory
-(UIControl *) createMenuItem:(Category *)cate;
-(void)menuSetOrReset;
-(void)topSetOrReset;
-(void)populateMenu;
-(void)loadNavigationItem;
-(void)clickSearch:(id)sender;
-(void)onClickUIImage:(UITapGestureRecognizer *)sender;
-(void)onClickCateLable:(UITapGestureRecognizer *)sender;
//点击top商家进入详细页面
-(void)onClickTopImage:(NSString *)mid;
-(void)pushMerchartDetail:(NSString *)cateType andNavController:(UINavigationController *)navController;

@end
