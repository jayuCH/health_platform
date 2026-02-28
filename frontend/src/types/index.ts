export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

export interface PageResult<T> {
  records: T[]
  total: number
  current: number
  size: number
  pages: number
}

// 用户相关类型
export interface LoginRequest {
  phone: string
  code: string
}

export interface UserInfo {
  id: string
  nickname: string
  avatar: string
  phone: string
  healthGoal: HealthGoal
}

export interface HealthGoal {
  id: string
  userId: string
  targetWeight: number
  currentWeight: number
  height: number
  targetCalories: number
  activityLevel: 'low' | 'medium' | 'high'
  dietType: 'balanced' | 'low-carb' | 'high-protein' | 'vegetarian'
  createdAt: string
  updatedAt: string
}

// 食谱相关类型
export interface Recipe {
  id: string
  name: string
  description: string
  coverImage: string
  calories: number
  protein: number
  fat: number
  carbohydrate: number
  categoryId: string
  categoryName: string
  cookingTime: number
  difficulty: 'easy' | 'medium' | 'hard'
  tags: string[]
  steps: RecipeStep[]
  ingredients: RecipeIngredient[]
  rating: number
  ratingCount: number
  viewCount: number
  isCollected: boolean
  createdAt: string
}

export interface RecipeStep {
  step: number
  description: string
  image: string
}

export interface RecipeIngredient {
  name: string
  amount: string
}

export interface RecipeCategory {
  id: string
  name: string
  icon: string
  sort: number
}

// 食物分析相关类型
export interface FoodAnalysis {
  id: string
  userId: string
  image: string
  foods: AnalyzedFood[]
  totalCalories: number
  totalProtein: number
  totalFat: number
  totalCarbohydrate: number
  analysisTime: string
  aiModel: string
}

export interface AnalyzedFood {
  name: string
  calories: number
  protein: number
  fat: number
  carbohydrate: number
  confidence: number
  boundingBox: BoundingBox
}

export interface BoundingBox {
  x: number
  y: number
  width: number
  height: number
}

// 健康档案相关类型
export interface HealthRecord {
  id: string
  userId: string
  recordDate: string
  weight: number
  bodyFat?: number
  muscle?: number
  water?: number
  sleepHours: number
  sleepQuality: 'poor' | 'fair' | 'good' | 'excellent'
  exerciseDuration: number
  exerciseType: string
  exerciseCalories: number
  steps: number
  calorieIntake: number
  waterIntake: number
  notes?: string
  createdAt: string
}

// 资讯相关类型
export interface Article {
  id: string
  title: string
  summary: string
  coverImage: string
  content: string
  author: string
  viewCount: number
  likeCount: number
  isLiked: boolean
  publishDate: string
  categoryId: string
  categoryName: string
  tags: string[]
}

// 轮播图相关类型
export interface Banner {
  id: string
  title: string
  image: string
  link: string
  linkType: 'url' | 'recipe' | 'article' | 'none'
  sort: number
  status: 'active' | 'inactive'
}

// AI生成相关类型
export interface AiRecipeRequest {
  ingredients?: string[]
  preferences?: string
  dietType?: 'balanced' | 'low-carb' | 'high-protein' | 'vegetarian'
  mealType?: 'breakfast' | 'lunch' | 'dinner' | 'snack'
  calories?: number
}

export interface AiRecipeResponse {
  id: string
  name: string
  description: string
  calories: number
  protein: number
  fat: number
  carbohydrate: number
  cookingTime: number
  ingredients: RecipeIngredient[]
  steps: RecipeStep[]
  nutritionTips: string[]
  warning?: string
  generationId: string
}
