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
#import "UICustomImageView.h"
#import "CityViewController.h"

@interface HomeViewController : UIViewController<CityViewDelegate>{
    NSMutableArray *_categoryArray;
    NSMutableArray *_topArray;
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
}

@property (nonatomic, strong) UIScrollView *menuView;
@property (nonatomic, assign) BOOL wrap;
@property (nonatomic, strong) NSMutableArray *items;

//@property (nonatomic, strong) iCarousel *carousel;
-(void)requestTopData;
-(void)requestMenuData;
-(UIScrollView *) setInitWithColumns:(int)col marginSize:(CGFloat)margin gutterSize:(CGFloat)gutter rowHeight:(CGFloat)height;
//创建catogory
-(UIControl *) createMenuItem:(Category *)cate;
//创建top商家
-(UIControl *) createTopItem :(MerchartModel *)model;
-(void)menuSetOrReset;
-(void)populateMenu;
-(void)loadNavigationItem;
-(void)clickSearch:(id)sender;
-(void)onClickUIImage:(UITapGestureRecognizer *)sender;
-(void)onClickUILable:(UITapGestureRecognizer *)sender;
//点击top商家进入详细页面
-(void)onClickTopImage:(UITapGestureRecognizer *)sender;
-(void)pushMerchartDetail:(NSString *)cateType andNavController:(UINavigationController *)navController;

@end
