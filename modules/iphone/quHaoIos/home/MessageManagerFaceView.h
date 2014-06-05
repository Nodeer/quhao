//
//  MessageManagerFaceView.h
//  quHaoIos
//
//  Created by sam on 14-6-4.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "FacialView.h"

#define  facialViewWidth 300
#define facialViewHeight 170
#define RGBACOLOR(r,g,b,a) [UIColor colorWithRed:(r)/255.0 green:(g)/255.0 blue:(b)/255.0 alpha:(a)]

@protocol MessageManagerFaceViewDelegate <NSObject>

- (void)SendTheFaceStr:(NSString *)faceStr;

@end

@interface MessageManagerFaceView : UIView<UIScrollViewDelegate,facialViewDelegate>
{
    UIScrollView *scrollView;
}
@property (nonatomic,weak)id<MessageManagerFaceViewDelegate>delegate;

@end
