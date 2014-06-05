//
//  MessageManagerFaceView.m
//  quHaoIos
//
//  Created by sam on 14-6-4.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import "MessageManagerFaceView.h"
#import "ExpressionSectionBar.h"

#define FaceSectionBarHeight  36   // 表情下面控件
#define FacePageControlHeight 20  // 表情pagecontrol

#define Pages 5

@implementation MessageManagerFaceView
{
    UIPageControl *pageControl;
}

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self setup];
    }
    return self;
}

- (void)setup{
    
    self.backgroundColor = [UIColor colorWithRed:248.0f/255 green:248.0f/255 blue:255.0f/255 alpha:1.0];
    
    //UIScrollView *scrollView = [[UIScrollView alloc]initWithFrame:CGRectMake(0.0f,0.0f,CGRectGetWidth(self.bounds),CGRectGetHeight(self.bounds)-FacePageControlHeight-FaceSectionBarHeight)];
    scrollView = [[UIScrollView alloc]initWithFrame:CGRectMake(0.0f,0.0f,CGRectGetWidth(self.bounds),CGRectGetHeight(self.bounds)-FacePageControlHeight)];
    scrollView.delegate = self;
    [self addSubview:scrollView];
    [scrollView setPagingEnabled:YES];
    [scrollView setShowsHorizontalScrollIndicator:NO];
    [scrollView setContentSize:CGSizeMake(CGRectGetWidth(scrollView.frame)*Pages,CGRectGetHeight(scrollView.frame))];
    [scrollView setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"facesBack"]]];
    for (int i= 0;i<Pages;i++) {
        //        ZBFaceView *faceView = [[ZBFaceView alloc]initWithFrame:CGRectMake(i*CGRectGetWidth(self.bounds),0.0f,CGRectGetWidth(self.bounds),CGRectGetHeight(scrollView.bounds)) forIndexPath:i];
        //        [scrollView addSubview:faceView];
        //        faceView.delegate = self;
        
        FacialView *fview=[[FacialView alloc] initWithFrame:CGRectMake(12+kDeviceWidth*i, 5, facialViewWidth, facialViewHeight)];
        [fview setBackgroundColor:[UIColor clearColor]];
        [fview loadFacialView:i size:CGSizeMake(33, 43)];
        fview.delegate=self;
        [scrollView addSubview:fview];
    }
    
    pageControl = [[UIPageControl alloc]init];
    [pageControl setFrame:CGRectMake(0,CGRectGetMaxY(scrollView.frame),CGRectGetWidth(self.bounds),FacePageControlHeight)];
    [pageControl setCurrentPage:0];
    pageControl.pageIndicatorTintColor=RGBACOLOR(195, 179, 163, 1);
    pageControl.currentPageIndicatorTintColor=RGBACOLOR(132, 104, 77, 1);
    pageControl.numberOfPages = Pages;//指定页面个数
    [pageControl setBackgroundColor:[UIColor clearColor]];
    [pageControl addTarget:self action:@selector(changePage:)forControlEvents:UIControlEventValueChanged];
    [self addSubview:pageControl];
    //    ZBExpressionSectionBar *sectionBar = [[ZBExpressionSectionBar alloc]initWithFrame:CGRectMake(0.0f,CGRectGetMaxY(pageControl.frame),CGRectGetWidth(self.bounds), FaceSectionBarHeight)];
    //    [self addSubview:sectionBar];
}

- (void)changePage:(id)sender {
    long page = pageControl.currentPage;//获取当前pagecontroll的值
    [scrollView setContentOffset:CGPointMake(kDeviceWidth * page, 0)];//根据pagecontroll的值来改变scrollview的滚动位置，以此切换到指定的页面
}


#pragma mark  scrollView Delegate
-(void)scrollViewDidScroll:(UIScrollView *)scrollView2
{
    int page = scrollView2.contentOffset.x/kDeviceWidth;
    pageControl.currentPage = page;
    
}

-(void)selectedFacialView:(NSString*)str
{
    if ([self.delegate respondsToSelector:@selector(SendTheFaceStr:) ]) {
        [self.delegate SendTheFaceStr:str];
    }
}

@end
