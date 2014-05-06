//
//  EScrollerView.h
//  icoiniPad
//
//  Created by Ethan on 12-11-24.
//
//

#import <UIKit/UIKit.h>
#import "EGOImageView.h"
#import "Helper.h"
#import "NSTimer+Addition.h"
@protocol EScrollerViewDelegate <NSObject>
@optional
-(void)EScrollerViewDidClicked:(NSUInteger)index;
@end

@interface EScrollerView : UIView<UIScrollViewDelegate> {
	CGRect viewSize;
	UIScrollView *scrollView;
	NSArray *imageArray;
    NSArray *titleArray;
    UIPageControl *pageControl;
    id<EScrollerViewDelegate> delegate;
    int currentPageIndex;
    UILabel *noteTitle;
    int timerCount;
}
@property (nonatomic , strong) NSTimer *animationTimer;
@property(nonatomic,retain)id<EScrollerViewDelegate> delegate;
-(id)initWithFrameRect:(CGRect)rect ImageArray:(NSArray *)imgArr TitleArray:(NSArray *)titArr;
@end
