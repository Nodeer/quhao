//
//  CustomToolbar.m
//  quHaoIos
//
//  Created by sam on 14-2-5.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import "CustomToolbar.h"
#import "Helper.h"
@implementation CustomToolbar

- (id)initWithFrame:(CGRect)aRect {
    if ((self = [super initWithFrame:aRect])){
        self.opaque = NO;
        CGSize size=CGSizeMake(110,30);
        [self setBackgroundImage:[Helper reSizeImage:@"dl.png" toSize:size] forToolbarPosition:0 barMetrics:0];
        self.clearsContextBeforeDrawing = YES;
    }
    return self;
}
/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    // Drawing code 91d3f5
}
*/

@end
