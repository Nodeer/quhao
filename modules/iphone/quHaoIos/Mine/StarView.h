//
//  StarView.h
//  quHaoIos
//
//  Created by sam on 13-11-13.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#define kEdgeInsetBottom 10

@interface StarView : UIButton {
    
}

- (id)initWithDefault:(UIImage*)star highlighted:(UIImage*)highlightedStar position:(int)index allowFractions:(BOOL)fractions;
- (void)centerIn:(CGRect)_frame with:(int)numberOfStars;
- (void)setStarImage:(UIImage*)starImage highlightedStarImage:(UIImage*)highlightedImage;
- (UIImage *)croppedImage:(UIImage*)image;
@end
