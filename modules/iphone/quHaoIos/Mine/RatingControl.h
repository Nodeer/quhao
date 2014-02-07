//
//  RatingControl.h
//  quHaoIos
//
//  Created by sam on 13-11-13.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#define kDefaultNumberOfStars 5
#define kNumberOfFractions 10

@protocol RatingDelegate;
@interface RatingControl : UIControl{
	int numberOfStars;
	int currentIdx;
	UIImage *star;
	UIImage *highlightedStar;
	IBOutlet id<RatingDelegate> delegate;
    BOOL isFractionalRatingEnabled;
}
- (id)initWithFrame:(CGRect)frame;
- (id)initWithFrame:(CGRect)frame andStars:(NSUInteger)_numberOfStars isFractional:(BOOL)isFract;
- (void)setStar:(UIImage*)defaultStarImage highlightedStar:(UIImage*)highlightedStarImage atIndex:(int)index;

@property (strong,nonatomic) UIImage *star;
@property (strong,nonatomic) UIImage *highlightedStar;
@property (nonatomic) float rating;
@property (strong,nonatomic) id<RatingDelegate> delegate;
@property (nonatomic,assign) BOOL isFractionalRatingEnabled;

@end

@protocol RatingDelegate

-(void)newRating:(RatingControl *)control :(float)rating;

@end
