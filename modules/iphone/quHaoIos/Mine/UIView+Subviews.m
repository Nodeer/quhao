//
//  UIView
//  quHaoIos
//
//  Created by sam on 13-11-10.
//  Copyright (c) 2013年 sam. All rights reserved.
//
#import "UIView+Subviews.h"


@implementation UIView (Subviews)

- (UIView*)subViewWithTag:(NSInteger)tag {
	for (UIView *v in self.subviews) {
		if (v.tag == tag) {
			return v;
		}
	}
	return nil;
}

@end
